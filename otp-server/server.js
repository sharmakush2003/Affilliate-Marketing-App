// © 2026 Reward Club — Secure OTP Backend Server
// Deploy FREE on Render.com (render.com/new → Web Service → link this repo)
// ✅ SMTP credentials NEVER enter the Android APK — only live here in .env
// ✅ OTPs stored in server memory with 5-min TTL
// ✅ Rate limited: max 3 OTP requests per email per 10 minutes
// ✅ Max 5 verify attempts per OTP before auto-delete
// ✅ One-time use: OTP deleted immediately after successful verify

require('dotenv').config();
const express = require('express');
const nodemailer = require('nodemailer');
const rateLimit = require('express-rate-limit');
const crypto = require('crypto');

const app = express();
const PORT = process.env.PORT || 4000;

// ── Validate required env vars on startup ─────────────────────────────────────
const SMTP_EMAIL    = process.env.SMTP_EMAIL;
const SMTP_PASSWORD = process.env.SMTP_PASSWORD;
const API_SECRET    = process.env.API_SECRET; // Shared secret between server & app

if (!SMTP_EMAIL || !SMTP_PASSWORD || !API_SECRET) {
  console.error('❌ FATAL: SMTP_EMAIL, SMTP_PASSWORD and API_SECRET must be set in .env');
  process.exit(1);
}

app.use(express.json({ limit: '10kb' }));

// ── CORS: only Android app (no browser origin) ───────────────────────────────
app.use((req, res, next) => {
  // Android HTTP clients don't send Origin header — allow all origins
  // but protect with API_SECRET header instead (see requireAppSecret)
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, X-Api-Secret');
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});

// ── Middleware: Verify shared app secret header ───────────────────────────────
// This prevents random internet users from spamming /send-otp
// Even if the secret leaks, worst case = someone can request OTPs (not read them)
function requireAppSecret(req, res, next) {
  const secret = req.headers['x-api-secret'];
  if (!secret || secret !== API_SECRET) {
    return res.status(401).json({ error: 'Unauthorized.' });
  }
  next();
}

// ── Rate Limiter: Max 3 OTP requests per email per 10 minutes ─────────────────
const otpSendLimiter = rateLimit({
  windowMs: 10 * 60 * 1000,
  max: 3,
  keyGenerator: (req) => (req.body?.email || req.ip).toLowerCase(),
  message: { error: 'Too many OTP requests. Please wait 10 minutes before trying again.' },
  standardHeaders: true,
  legacyHeaders: false,
});

// ── In-Memory OTP Store with TTL ──────────────────────────────────────────────
// Structure: Map<email, { otp, expiresAt, attempts, maxAttempts }>
// For production scale → swap this with Redis. For Reward Club's current scale = perfect.
const otpStore = new Map();

// Auto-cleanup expired OTPs every 2 minutes
setInterval(() => {
  const now = Date.now();
  for (const [email, data] of otpStore.entries()) {
    if (data.expiresAt < now) otpStore.delete(email);
  }
}, 2 * 60 * 1000);

// ── Email transporter (Gmail SMTP — credentials ONLY here, never in APK) ──────
function createTransporter() {
  return nodemailer.createTransport({
    service: 'gmail',
    auth: { user: SMTP_EMAIL, pass: SMTP_PASSWORD },
  });
}

// ── Validate email format ──────────────────────────────────────────────────────
function isValidEmail(email) {
  return /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);
}

// ── Generate cryptographically secure 4-digit OTP ─────────────────────────────
function generateOtp() {
  return String(crypto.randomInt(1000, 10000)).padStart(4, '0');
}

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/send-otp
// Body: { "email": "user@example.com" }
// Header: X-Api-Secret: <API_SECRET>
// ─────────────────────────────────────────────────────────────────────────────
app.post('/api/send-otp', requireAppSecret, otpSendLimiter, async (req, res) => {
  const email = (req.body?.email || '').trim().toLowerCase();

  if (!email || !isValidEmail(email)) {
    return res.status(400).json({ error: 'A valid email address is required.' });
  }

  const otp = generateOtp();
  const expiresAt = Date.now() + 5 * 60 * 1000; // 5 minutes

  // Store OTP
  otpStore.set(email, { otp, expiresAt, attempts: 0, maxAttempts: 5 });

  // Send email
  const transporter = createTransporter();
  const mailOptions = {
    from: `"Reward Club" <${SMTP_EMAIL}>`,
    to: email,
    subject: 'Reward Club — Sign In Verification OTP',
    text: `Hello,\n\nYour Reward Club verification code is:\n\n${otp}\n\nThis code expires in 5 minutes. Never share it with anyone.\n\nReward Club Team`,
    html: `
<div style="font-family:Arial,sans-serif;max-width:480px;margin:auto;border:1px solid #e0e0e0;border-radius:12px;overflow:hidden;">
  <div style="background:linear-gradient(135deg,#1B5E20,#2E7D32);padding:32px;text-align:center;">
    <h1 style="color:white;margin:0;font-size:24px;">Reward Club</h1>
    <p style="color:rgba(255,255,255,0.8);margin:8px 0 0 0;font-size:14px;">Your Verification Code</p>
  </div>
  <div style="padding:32px;text-align:center;">
    <p style="color:#333;font-size:15px;">Use the code below to sign in to your account.</p>
    <div style="background:#f5f5f5;border-radius:8px;padding:20px;margin:24px 0;letter-spacing:12px;font-size:36px;font-weight:bold;color:#1B5E20;">${otp}</div>
    <p style="color:#999;font-size:13px;">Expires in <strong>5 minutes</strong>.</p>
    <p style="color:#bbb;font-size:12px;margin-top:24px;">Never share this code. Reward Club staff will never ask for it.</p>
  </div>
  <div style="background:#fafafa;padding:16px;text-align:center;border-top:1px solid #eee;">
    <p style="color:#ccc;font-size:11px;margin:0;">© 2026 Reward Club. All rights reserved.</p>
  </div>
</div>`,
  };

  try {
    await transporter.sendMail(mailOptions);
    console.log(`[OTP] Sent to ${email.replace(/(.{2}).*(@.*)/, '$1***$2')}`); // log masked email only
    return res.json({ success: true, message: 'OTP sent to your email address.' });
  } catch (err) {
    console.error('[OTP] Email send failed:', err.message);
    otpStore.delete(email); // clean up on failure
    return res.status(500).json({ error: 'Failed to send OTP email. Please try again.' });
  }
});

// ─────────────────────────────────────────────────────────────────────────────
// POST /api/verify-otp
// Body: { "email": "user@example.com", "otp": "1234" }
// Header: X-Api-Secret: <API_SECRET>
// ─────────────────────────────────────────────────────────────────────────────
app.post('/api/verify-otp', requireAppSecret, async (req, res) => {
  const email       = (req.body?.email || '').trim().toLowerCase();
  const submittedOtp = (req.body?.otp   || '').trim();

  if (!email || !isValidEmail(email)) {
    return res.status(400).json({ error: 'A valid email address is required.' });
  }
  if (!submittedOtp || !/^\d{4}$/.test(submittedOtp)) {
    return res.status(400).json({ error: 'OTP must be a 4-digit number.' });
  }

  const record = otpStore.get(email);

  // Not found
  if (!record) {
    return res.status(404).json({ error: 'OTP not found or already used. Please request a new OTP.' });
  }

  // Expired
  if (Date.now() > record.expiresAt) {
    otpStore.delete(email);
    return res.status(410).json({ error: 'OTP has expired. Please request a new one.' });
  }

  // Attempt limit exceeded (server-side brute-force protection)
  if (record.attempts >= record.maxAttempts) {
    otpStore.delete(email);
    return res.status(429).json({ error: 'Too many wrong attempts. Please request a new OTP.' });
  }

  // Wrong OTP
  if (submittedOtp !== record.otp) {
    record.attempts++;
    const remaining = record.maxAttempts - record.attempts;
    return res.status(401).json({
      error: `Incorrect OTP. ${remaining} attempt(s) remaining.`,
      remaining,
    });
  }

  // ✅ Correct — delete immediately (one-time use)
  otpStore.delete(email);
  console.log(`[OTP] Verified successfully for ${email.replace(/(.{2}).*(@.*)/, '$1***$2')}`);
  return res.json({ success: true, verified: true });
});

// ── Health check ──────────────────────────────────────────────────────────────
app.get('/health', (req, res) => {
  res.json({ status: 'ok', service: 'Reward Club OTP Server', time: new Date().toISOString() });
});

app.listen(PORT, () => {
  console.log(`\n✅ Reward Club OTP Server running on port ${PORT}`);
  console.log(`📧 SMTP: ${SMTP_EMAIL}`);
  console.log(`🔒 API Secret: ${API_SECRET.substring(0, 6)}...`);
});
