import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { SmtpClient } from "https://deno.land/x/smtp/mod.ts"

const SMTP_EMAIL = Deno.env.get("SMTP_EMAIL") || "rewardclub.team@gmail.com";
const SMTP_PASSWORD = Deno.env.get("SMTP_PASSWORD") || "trwqwwxttewvvuoi";

serve(async (req) => {
  // Handle CORS
  if (req.method === 'OPTIONS') {
    return new Response('ok', {
      headers: {
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Headers': 'authorization, x-client-info, apikey, content-type',
      }
    })
  }

  try {
    // Parse the webhook payload from Supabase database trigger
    const payload = await req.json()
    console.log("Received payload:", payload)

    // The webhook payload schema is: { type: 'INSERT', table: 'support_queries', record: { ... } }
    const record = payload.record

    if (!record) {
      return new Response(JSON.stringify({ error: "No record found in payload" }), {
        status: 400,
        headers: { "Content-Type": "application/json" }
      })
    }

    const { name, email, mobile, user_id, feedback_type, rating, message } = record

    // Initialize SMTP client and connect
    const client = new SmtpClient()
    await client.connectTLS({
      hostname: "smtp.gmail.com",
      port: 465,
      username: SMTP_EMAIL,
      password: SMTP_PASSWORD,
    })

    // Send email using custom HTML body matching your design style
    await client.send({
      from: SMTP_EMAIL,
      to: "dhakarpuran@gmail.com",
      bcc: ["chittortech@gmail.com"],
      subject: `Support Query - ${feedback_type || "General"} - ${name || "User"}`,
      content: `Hello Reward Club Support Team,\n\nI have a support query.\n\nQuery Type: ${feedback_type || "General"}\nRating: ${rating || 0} / 5\n\nMessage:\n${message}\n\nUser Details:\n- Name: ${name || "N/A"}\n- Email: ${email || "N/A"}\n- Mobile: ${mobile || "N/A"}\n- User ID: ${user_id || "N/A"}\n\nRegards,\n${name || "User"}`,
      html: `
<div style="font-family:Arial,sans-serif;max-width:550px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden;">
  <div style="background:linear-gradient(135deg,#1B5E20,#2E7D32);padding:24px;text-align:center;">
    <h2 style="color:white;margin:0;font-size:20px;">New Support Request</h2>
    <p style="color:rgba(255,255,255,0.8);margin:4px 0 0 0;font-size:13px;">Reward Club App Customer Care</p>
  </div>
  <div style="padding:24px;">
    <table style="width:100%;border-collapse:collapse;margin-bottom:20px;">
      <tr style="background:#f9f9f9;"><td style="padding:10px;font-weight:bold;width:30%;">Query Type:</td><td style="padding:10px;">${feedback_type || 'General'}</td></tr>
      <tr><td style="padding:10px;font-weight:bold;">User Rating:</td><td style="padding:10px;">${rating || 0} / 5</td></tr>
      <tr style="background:#f9f9f9;"><td style="padding:10px;font-weight:bold;">User Name:</td><td style="padding:10px;">${name || 'N/A'}</td></tr>
      <tr><td style="padding:10px;font-weight:bold;">User Email:</td><td style="padding:10px;">${email || 'N/A'}</td></tr>
      <tr style="background:#f9f9f9;"><td style="padding:10px;font-weight:bold;">User Mobile:</td><td style="padding:10px;">${mobile || 'N/A'}</td></tr>
      <tr><td style="padding:10px;font-weight:bold;">User ID:</td><td style="padding:10px;">${user_id || 'N/A'}</td></tr>
    </table>
    
    <div style="background:#f5f5f5;border-left:4px solid #1B5E20;padding:16px;margin-top:20px;border-radius:0 8px 8px 0;">
      <p style="margin:0 0 8px 0;font-weight:bold;color:#1B5E20;">Message:</p>
      <p style="margin:0;white-space:pre-wrap;color:#333;line-height:22px;">${message}</p>
    </div>
  </div>
  <div style="background:#fafafa;padding:16px;text-align:center;border-top:1px solid #eee;">
    <p style="color:#ccc;font-size:11px;margin:0;">© 2026 Reward Club. All rights reserved.</p>
  </div>
</div>`,
    })

    await client.close()
    console.log("Email sent successfully!")

    return new Response(JSON.stringify({ success: true, message: "Email sent successfully" }), {
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" }
    })
  } catch (error) {
    console.error("Error sending email:", error)
    return new Response(JSON.stringify({ error: error.message }), {
      status: 500,
      headers: { "Content-Type": "application/json", "Access-Control-Allow-Origin": "*" }
    })
  }
})
