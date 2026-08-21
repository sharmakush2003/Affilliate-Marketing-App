import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'
import nodemailer from 'nodemailer'

const SMTP_USER = process.env.SMTP_USER!
const SMTP_PASSWORD = process.env.SMTP_PASSWORD!

// Create nodemailer transporter
const transporter = nodemailer.createTransport({
  host: 'smtp.gmail.com',
  port: 587,
  secure: false, // true for 465, false for 587 (STARTTLS)
  auth: {
    user: SMTP_USER,
    pass: SMTP_PASSWORD,
  },
})

export async function POST(req: NextRequest) {
  try {
    const { email } = await req.json()

    if (!email) {
      return NextResponse.json({ error: 'Email address is required' }, { status: 400 })
    }

    const trimmedEmail = email.trim().toLowerCase()

    // 1. Verify if user has a profile in public.profiles (to ensure they exist in our database)
    const { data: profile, error: profileErr } = await supabaseAdmin
      .from('profiles')
      .select('id, email')
      .eq('email', trimmedEmail)
      .single()

    // If not found in profiles, we still return success 200 (to prevent user enumeration attacks)
    // but we do NOT send the email. This matches standard security policies.
    if (profileErr || !profile) {
      console.log(`[forgot-password] Email ${trimmedEmail} not found in public.profiles. Skipping email dispatch for security.`)
      return NextResponse.json({ success: true, message: 'Recovery email dispatched if account exists.' })
    }

    // 2. Generate Supabase recovery confirmation link server-side (bypasses default email templates)
    const origin = req.headers.get('origin') || new URL(req.url).origin
    const { data: linkData, error: linkErr } = await supabaseAdmin.auth.admin.generateLink({
      type: 'recovery',
      email: trimmedEmail,
      options: {
        redirectTo: `${origin}/reset-password`,
      },
    })

    if (linkErr || !linkData?.properties?.action_link) {
      console.error('[forgot-password] Supabase generateLink failed:', linkErr?.message)
      return NextResponse.json({ error: 'Failed to generate reset link' }, { status: 500 })
    }

    const actionLink = linkData.properties.action_link

    // 3. Construct premium, admin-branded HTML email template
    const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Reset your Reward Club Admin Password</title>
      <style>
        body {
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
          background-color: #f9fafb;
          margin: 0;
          padding: 0;
        }
        .wrapper {
          width: 100%;
          background-color: #f9fafb;
          padding: 40px 0;
        }
        .container {
          max-width: 500px;
          margin: 0 auto;
          background-color: #ffffff;
          border: 1px solid #e2e8f0;
          border-radius: 16px;
          overflow: hidden;
          box-shadow: 0 4px 12px rgba(15, 23, 42, 0.03);
        }
        .header {
          background-color: #0f172a;
          padding: 32px;
          text-align: center;
          border-bottom: 4px solid #10b981;
        }
        .logo-text {
          color: #ffffff;
          font-size: 22px;
          font-weight: 800;
          letter-spacing: -0.5px;
          margin: 0;
        }
        .logo-sub {
          color: #10b981;
          font-size: 9px;
          font-weight: 700;
          letter-spacing: 2px;
          text-transform: uppercase;
          margin-top: 4px;
          display: block;
        }
        .content {
          padding: 32px;
        }
        .greeting {
          font-size: 16px;
          font-weight: 700;
          color: #0f172a;
          margin-top: 0;
          margin-bottom: 12px;
        }
        .text {
          font-size: 14px;
          color: #475569;
          line-height: 1.6;
          margin-top: 0;
          margin-bottom: 24px;
        }
        .button-container {
          text-align: center;
          margin-bottom: 24px;
        }
        .button {
          display: inline-block;
          background-color: #0f172a;
          color: #ffffff !important;
          text-decoration: none;
          font-size: 14px;
          font-weight: 700;
          padding: 12px 24px;
          border-radius: 8px;
        }
        .warning-box {
          background-color: #fef2f2;
          border: 1px solid #fee2e2;
          border-radius: 8px;
          padding: 12px 16px;
          margin-bottom: 24px;
        }
        .warning-text {
          font-size: 12px;
          color: #991b1b;
          line-height: 1.5;
          margin: 0;
        }
        .footer {
          background-color: #f8fafc;
          padding: 24px 32px;
          text-align: center;
          border-top: 1px solid #f1f5f9;
        }
        .footer-text {
          font-size: 11px;
          color: #94a3b8;
          margin: 0 0 4px 0;
          text-transform: uppercase;
          letter-spacing: 0.5px;
          font-weight: 600;
        }
        .footer-subtext {
          font-size: 10px;
          color: #64748b;
          margin: 0;
        }
      </style>
    </head>
    <body>
      <div class="wrapper">
        <div class="container">
          <div class="header">
            <h1 class="logo-text">Reward Club</h1>
            <span class="logo-sub">Admin Portal</span>
          </div>
          <div class="content">
            <p class="greeting">Hello,</p>
            <p class="text">
              A request has been received to reset the password for your Reward Club administrator account.
            </p>
            <div class="button-container">
              <a href="${actionLink}" class="button" style="color: #ffffff;">Reset Admin Password</a>
            </div>
            <div class="warning-box">
              <p class="warning-text">
                <strong>Security Warning:</strong> If you did not authorize this password reset request, please ignore this email or notify security immediately. This link is secure and will expire shortly.
              </p>
            </div>
          </div>
          <div class="footer">
            <p class="footer-text">Secure Admin Panel Access</p>
            <p class="footer-subtext">Managed and Protected by ChittorTech</p>
          </div>
        </div>
      </div>
    </body>
    </html>
    `

    // 4. Send email via SMTP
    await transporter.sendMail({
      from: `"Reward Club" <${SMTP_USER}>`,
      to: trimmedEmail,
      subject: 'Reset your Reward Club Admin Password',
      html: htmlContent,
    })

    return NextResponse.json({ success: true, message: 'Recovery email dispatched successfully.' })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown SMTP error'
    console.error('[forgot-password] API error:', errorMsg)
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}
