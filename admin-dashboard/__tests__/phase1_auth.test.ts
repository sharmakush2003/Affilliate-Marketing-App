/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  REWARD CLUB — PHASE 1: User Onboarding & Auth Flow (0%→15%)   ║
 * ╚══════════════════════════════════════════════════════════════════╝
 *
 * Run: npx vitest run __tests__/phase1_auth.test.ts
 */

import { describe, it, expect, afterEach } from 'vitest'
import { createClient } from '@supabase/supabase-js'

// ── Load env from .env.local ────────────────────────────────────────────────
import { config } from 'dotenv'
import { resolve } from 'path'
config({ path: resolve(process.cwd(), '.env.local') })

const SUPABASE_URL  = process.env.NEXT_PUBLIC_SUPABASE_URL!
const SUPABASE_SVC  = process.env.SUPABASE_SERVICE_ROLE_KEY!
const BASE_URL      = process.env.ADMIN_BASE_URL || 'http://localhost:3000'

if (!SUPABASE_URL || !SUPABASE_SVC) {
  throw new Error('❌ Set NEXT_PUBLIC_SUPABASE_URL and SUPABASE_SERVICE_ROLE_KEY in .env.local')
}

const admin = createClient(SUPABASE_URL, SUPABASE_SVC, {
  auth: { autoRefreshToken: false, persistSession: false }
})

const uid = () => `qa_${Date.now()}_${Math.random().toString(36).slice(2)}`
const cleanup: string[] = []

afterEach(async () => {
  for (const id of cleanup.splice(0)) {
    await admin.auth.admin.deleteUser(id).catch(() => {})
  }
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T1.1 — Registration & on_auth_user_created Trigger', () => {

  it('✅ T1.1.1 — New user signup auto-creates profiles row with 0 coins', async () => {
    const email = `${uid()}@qamailtest.com`
    const { data, error } = await admin.auth.admin.createUser({
      email,
      password: 'QaPass@1234',
      email_confirm: true,
      user_metadata: { full_name: 'QA Tester', mobile: '9000000001' },
    })
    expect(error).toBeNull()
    cleanup.push(data.user!.id)

    // Give trigger up to 2s
    await new Promise(r => setTimeout(r, 2000))

    const { data: profile, error: pErr } = await admin
      .from('profiles')
      .select('id, total_coins, redeemed_coins, total_savings, email')
      .eq('id', data.user!.id)
      .single()

    expect(pErr).toBeNull()
    expect(profile!.id).toBe(data.user!.id)
    expect(profile!.total_coins).toBe(0)
    expect(profile!.redeemed_coins).toBe(0)
    expect(profile!.email).toBe(email)
    console.log('✅ T1.1.1 PASS — Profile created:', profile!.id)
  }, 15_000)

  it('✅ T1.1.2 — Trigger is idempotent (upsert on same user does not error)', async () => {
    const email = `${uid()}@qamailtest.com`
    const { data } = await admin.auth.admin.createUser({
      email, password: 'QaPass@1234', email_confirm: true,
    })
    cleanup.push(data.user!.id)
    await new Promise(r => setTimeout(r, 1500))

    // Force second upsert — simulates re-run
    const { error } = await admin.from('profiles').upsert({ id: data.user!.id, email })
    expect(error).toBeNull()

    const { count } = await admin
      .from('profiles')
      .select('*', { count: 'exact', head: true })
      .eq('id', data.user!.id)
    expect(count).toBe(1)
    console.log('✅ T1.1.2 PASS — Idempotent upsert safe')
  }, 15_000)

  it('✅ T1.1.3 — Deleting auth user cascades deletion in profiles', async () => {
    const email = `${uid()}@qamailtest.com`
    const { data } = await admin.auth.admin.createUser({
      email, password: 'QaPass@1234', email_confirm: true,
    })
    await new Promise(r => setTimeout(r, 1500))

    // Delete auth user — profiles FK ON DELETE CASCADE should remove profile
    await admin.auth.admin.deleteUser(data.user!.id)
    await new Promise(r => setTimeout(r, 1000))

    const { data: profile } = await admin
      .from('profiles').select('id').eq('id', data.user!.id).single()
    expect(profile).toBeNull()
    console.log('✅ T1.1.3 PASS — Cascade delete works')
  }, 15_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T1.2 — Admin Login API Route Security', () => {

  it('✅ T1.2.1 — Missing email/password → 400', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({}),
    })
    expect(res.status).toBe(400)
    const body = await res.json()
    expect(body.error).toMatch(/required/i)
    console.log('✅ T1.2.1 PASS — 400 on missing fields')
  })

  it('✅ T1.2.2 — Non-admin email → 403 Access Denied', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'hacker@evil.com', password: 'hack' }),
    })
    expect(res.status).toBe(403)
    const body = await res.json()
    expect(body.error).toMatch(/not.*authorized|access denied/i)
    console.log('✅ T1.2.2 PASS — 403 for unauthorized email')
  })

  it('✅ T1.2.3 — Response NEVER leaks session_token or access_token', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: 'hacker@evil.com', password: 'hack' }),
    })
    const body = await res.json()
    expect(body).not.toHaveProperty('session_token')
    expect(body).not.toHaveProperty('access_token')
    expect(body).not.toHaveProperty('refresh_token')
    console.log('✅ T1.2.3 PASS — No token leaked in error response')
  })
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T1.3 — OTP 2FA Verify-OTP Endpoint', () => {

  it('✅ T1.3.1 — Missing fields → 400', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({}),
    })
    expect(res.status).toBe(400)
    console.log('✅ T1.3.1 PASS — 400 on empty body')
  })

  it('✅ T1.3.2 — Invalid sessionId → 404', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: '00000000-dead-beef-dead-000000000000', otpCode: '123456' }),
    })
    expect(res.status).toBe(404)
    console.log('✅ T1.3.2 PASS — 404 for ghost session')
  })

  it('✅ T1.3.3 — Expired OTP (10 min ago) → 401', async () => {
    const expired = new Date(Date.now() - 10 * 60 * 1000).toISOString()
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'qa@test.com', ip_address: '127.0.0.1', location: 'Test',
      user_agent: 'Vitest', status: 'pending_2fa',
      otp_code: '999999', otp_expires: expired,
    }).select('id').single()

    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: s!.id, otpCode: '999999' }),
    })
    expect(res.status).toBe(401)
    const body = await res.json()
    expect(body.error).toMatch(/expired/i)

    await admin.from('admin_sessions').delete().eq('id', s!.id)
    console.log('✅ T1.3.3 PASS — Expired OTP rejected')
  }, 10_000)

  it('✅ T1.3.4 — Wrong OTP code → 401 invalid', async () => {
    const validExpiry = new Date(Date.now() + 5 * 60 * 1000).toISOString()
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'qa2@test.com', ip_address: '127.0.0.1', location: 'Test',
      user_agent: 'Vitest', status: 'pending_2fa',
      otp_code: '123456', otp_expires: validExpiry,
    }).select('id').single()

    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: s!.id, otpCode: '000000' }),
    })
    expect(res.status).toBe(401)
    const body = await res.json()
    expect(body.error).toMatch(/invalid/i)

    await admin.from('admin_sessions').delete().eq('id', s!.id)
    console.log('✅ T1.3.4 PASS — Wrong OTP rejected')
  }, 10_000)

  it('✅ T1.3.5 — Correct OTP within 5min → session becomes active, otp_code cleared', async () => {
    const validExpiry = new Date(Date.now() + 5 * 60 * 1000).toISOString()
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'qa3@test.com', ip_address: '127.0.0.1', location: 'Test',
      user_agent: 'Vitest', status: 'pending_2fa',
      otp_code: '654321', otp_expires: validExpiry,
    }).select('id').single()

    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify-otp`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ sessionId: s!.id, otpCode: '654321' }),
    })
    expect(res.status).toBe(200)
    expect((await res.json()).success).toBe(true)

    const { data: updated } = await admin
      .from('admin_sessions').select('status, otp_code').eq('id', s!.id).single()
    expect(updated!.status).toBe('active')
    expect(updated!.otp_code).toBeNull()

    await admin.from('admin_sessions').delete().eq('id', s!.id)
    console.log('✅ T1.3.5 PASS — Valid OTP activates session')
  }, 10_000)

  it('✅ T1.3.6 — Session verify endpoint: active+expired window → valid=false', async () => {
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'qa4@test.com', ip_address: '127.0.0.1', location: 'Test',
      user_agent: 'Vitest', status: 'active',
      otp_expires: new Date(Date.now() - 1000).toISOString(),
    }).select('id').single()

    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify?id=${s!.id}`)
    const body = await res.json()
    expect(body.valid).toBe(false)

    await admin.from('admin_sessions').delete().eq('id', s!.id)
    console.log('✅ T1.3.6 PASS — Expired active session marked invalid')
  }, 10_000)
})
