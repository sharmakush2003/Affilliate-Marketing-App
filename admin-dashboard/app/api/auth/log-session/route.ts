import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'
import nodemailer from 'nodemailer'

const SMTP_USER = process.env.SMTP_USER!
const SMTP_PASSWORD = process.env.SMTP_PASSWORD!

const transporter = nodemailer.createTransport({
  host: 'smtp.gmail.com',
  port: 587,
  secure: false,
  auth: { user: SMTP_USER, pass: SMTP_PASSWORD },
})

function generateOTP(): string {
  return Math.floor(100000 + Math.random() * 900000).toString()
}

export async function POST(req: NextRequest) {
  try {
    const { email, location, userAgent } = await req.json()

    const forwarded = req.headers.get('x-forwarded-for')
    const ipAddress = forwarded ? forwarded.split(',')[0] : '127.0.0.1'

    const otpCode = generateOTP()
    const otpExpires = new Date(Date.now() + 5 * 60 * 1000).toISOString() // 5 minutes

    const { data, error } = await supabaseAdmin
      .from('admin_sessions')
      .insert({
        email,
        ip_address: ipAddress,
        location: location || 'Unknown Location',
        user_agent: userAgent || 'Unknown Browser',
        status: 'pending_2fa',
        otp_code: otpCode,
        otp_expires: otpExpires,
      })
      .select('id')
      .single()

    if (error) {
      console.error('[log-session] db insert error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    // Send OTP email
    const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset="utf-8">
      <title>Admin Verification Code</title>
      <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #f9fafb; margin: 0; padding: 0; }
        .wrapper { width: 100%; background: #f9fafb; padding: 40px 0; }
        .container { max-width: 480px; margin: 0 auto; background: #fff; border: 1px solid #e2e8f0; border-radius: 16px; overflow: hidden; }
        .header { background: #0f172a; padding: 28px 32px; text-align: center; border-bottom: 4px solid #10b981; }
        .logo-text { color: #fff; font-size: 20px; font-weight: 800; letter-spacing: -0.5px; margin: 0; }
        .logo-sub { color: #10b981; font-size: 9px; font-weight: 700; letter-spacing: 2px; text-transform: uppercase; margin-top: 4px; display: block; }
        .content { padding: 32px; text-align: center; }
        .label { font-size: 14px; color: #64748b; margin: 0 0 16px 0; }
        .otp-box { display: inline-block; background: #f8fafc; border: 2px dashed #cbd5e1; border-radius: 12px; padding: 20px 40px; margin-bottom: 24px; }
        .otp-code { font-size: 40px; font-weight: 900; color: #0f172a; letter-spacing: 12px; font-family: 'Courier New', monospace; }
        .warning { font-size: 12px; color: #64748b; background: #fef9c3; border: 1px solid #fde68a; border-radius: 8px; padding: 10px 14px; margin-top: 8px; }
        .footer { background: #f8fafc; padding: 20px 32px; text-align: center; border-top: 1px solid #f1f5f9; }
        .footer-text { font-size: 10px; color: #94a3b8; margin: 0; text-transform: uppercase; letter-spacing: 0.5px; }
      </style>
    </head>
    <body>
      <div class="wrapper">
        <div class="container">
          <div class="header">
            <h1 class="logo-text">Reward Club</h1>
            <span class="logo-sub">Admin Portal — 2FA Verification</span>
          </div>
          <div class="content">
            <p class="label">Your one-time admin login verification code is:</p>
            <div class="otp-box">
              <div class="otp-code">${otpCode}</div>
            </div>
            <p class="warning">⏱ This code expires in <strong>5 minutes</strong>. Do not share it with anyone.</p>
          </div>
          <div class="footer">
            <p class="footer-text">Managed & Protected by ChittorTech · Reward Club Admin Portal</p>
          </div>
        </div>
      </div>
    </body>
    </html>`

    try {
      await transporter.sendMail({
        from: `"Reward Club Security" <${SMTP_USER}>`,
        to: email,
        subject: `${otpCode} — Your Admin Verification Code`,
        html: htmlContent,
      })
    } catch (mailErr: unknown) {
      const msg = mailErr instanceof Error ? mailErr.message : 'Mail error'
      console.error('[log-session] Email dispatch failed:', msg)
      // Still return sessionId so user knows session was created, but flag the issue
      return NextResponse.json({ sessionId: data.id, pending2FA: true, emailError: true })
    }

    return NextResponse.json({ sessionId: data.id, pending2FA: true })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown log-session error'
    console.error('[log-session] API error:', errorMsg)
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}

