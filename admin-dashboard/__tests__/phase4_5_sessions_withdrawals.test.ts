/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  REWARD CLUB — PHASE 4: Admin Sessions (55%→70%)               ║
 * ║  PHASE 5: Withdrawal Lifecycle (70%→85%)                       ║
 * ╚══════════════════════════════════════════════════════════════════╝
 */

import { describe, it, expect } from 'vitest'
import { createClient } from '@supabase/supabase-js'
import { config } from 'dotenv'
import { resolve } from 'path'
config({ path: resolve(process.cwd(), '.env.local') })

const SUPABASE_URL = process.env.NEXT_PUBLIC_SUPABASE_URL!
const SUPABASE_SVC = process.env.SUPABASE_SERVICE_ROLE_KEY!
const BASE_URL     = process.env.ADMIN_BASE_URL || 'http://localhost:3000'

if (!SUPABASE_URL || !SUPABASE_SVC) {
  throw new Error('❌ Missing env vars in .env.local')
}

const admin = createClient(SUPABASE_URL, SUPABASE_SVC, {
  auth: { autoRefreshToken: false, persistSession: false }
})

async function createTestUser(coins = 0) {
  const email = `qa_wd_${Date.now()}@mailtest.com`
  const { data } = await admin.auth.admin.createUser({
    email, password: 'QaPass@1234', email_confirm: true,
  })
  await new Promise(r => setTimeout(r, 1500))
  if (coins > 0) {
    await admin.from('profiles').update({ total_coins: coins }).eq('id', data.user!.id)
  }
  return { userId: data.user!.id, email }
}

// ─────────────────────────────────────────────────────────────────────────────
describe('T4.1 — Session Revocation', () => {

  it('✅ T4.1.1 — GET /api/auth/sessions returns list of sessions', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/sessions`)
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body).toHaveProperty('sessions')
    expect(Array.isArray(body.sessions)).toBe(true)
    console.log(`✅ T4.1.1 PASS — Sessions endpoint OK, count=${body.sessions.length}`)
  }, 10_000)

  it('✅ T4.1.2 — DELETE /api/auth/sessions without id → 400', async () => {
    const res = await fetch(`${BASE_URL}/api/auth/sessions`, { method: 'DELETE' })
    expect(res.status).toBe(400)
    console.log('✅ T4.1.2 PASS — DELETE without id → 400')
  }, 10_000)

  it('✅ T4.1.3 — DELETE session removes it from DB (revocation)', async () => {
    // Create a dummy session
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'revoke@test.com',
      ip_address: '127.0.0.1',
      location: 'Test',
      user_agent: 'Vitest',
      status: 'active',
    }).select('id').single()

    const sessionId = s!.id

    // Revoke via API
    const res = await fetch(`${BASE_URL}/api/auth/sessions?id=${sessionId}`, {
      method: 'DELETE',
    })
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.success).toBe(true)

    // Verify gone from DB
    const { data: deleted } = await admin
      .from('admin_sessions')
      .select('id')
      .eq('id', sessionId)
      .single()
    expect(deleted).toBeNull()

    console.log('✅ T4.1.3 PASS — Session revoked and removed from DB')
  }, 10_000)

  it('✅ T4.1.4 — Revoked session returns valid=false on verify', async () => {
    // Create then immediately delete session
    const { data: s } = await admin.from('admin_sessions').insert({
      email: 'revoke2@test.com',
      ip_address: '127.0.0.1',
      location: 'Test',
      user_agent: 'Vitest',
      status: 'active',
    }).select('id').single()

    await admin.from('admin_sessions').delete().eq('id', s!.id)

    const res = await fetch(`${BASE_URL}/api/auth/sessions/verify?id=${s!.id}`)
    const body = await res.json()
    expect(body.valid).toBe(false)
    console.log('✅ T4.1.4 PASS — Deleted session → valid=false')
  }, 10_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T5.1 — Withdrawal Minimum Threshold Validation', () => {

  it('✅ T5.1.1 — amountInr < 500 → 400 with minimum message', async () => {
    const { userId } = await createTestUser(6000)

    const res = await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId,
        amountInr: 100,         // Below ₹500 minimum
        coinsDeducted: 1000,    // Below 5000 coin minimum
        paymentMethod: 'UPI',
        payoutDetails: 'test@upi',
      }),
    })
    expect(res.status).toBe(400)
    const body = await res.json()
    expect(body.error).toMatch(/minimum|500/i)

    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T5.1.1 PASS — Below ₹500 rejected')
  }, 15_000)

  it('✅ T5.1.2 — coins < 5000 → 400', async () => {
    const { userId } = await createTestUser(6000)

    const res = await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId,
        amountInr: 600,
        coinsDeducted: 999,     // Below 5000 min
        paymentMethod: 'UPI',
        payoutDetails: 'test@upi',
      }),
    })
    expect(res.status).toBe(400)
    const body = await res.json()
    expect(body.error).toMatch(/minimum|5000/i)

    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T5.1.2 PASS — Below 5000 coins rejected')
  }, 15_000)

  it('✅ T5.1.3 — Insufficient balance → 400', async () => {
    const { userId } = await createTestUser(100) // Only 100 coins

    const res = await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId,
        amountInr: 500,
        coinsDeducted: 5000,
        paymentMethod: 'UPI',
        payoutDetails: 'test@upi',
      }),
    })
    expect(res.status).toBe(400)
    const body = await res.json()
    expect(body.error).toMatch(/insufficient/i)

    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T5.1.3 PASS — Insufficient balance rejected')
  }, 15_000)

  it('✅ T5.1.4 — Valid withdrawal (≥5000 coins, ≥₹500) → 200, status=pending', async () => {
    const { userId } = await createTestUser(10000)

    const res = await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId,
        amountInr: 500,
        coinsDeducted: 5000,
        paymentMethod: 'UPI',
        payoutDetails: 'success@upi',
        userName: 'QA Tester',
        userEmail: 'qa@test.com',
        userMobile: '9000000000',
      }),
    })
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.success).toBe(true)
    expect(body.withdrawal.status).toBe('pending')
    expect(body.withdrawal.user_id).toBe(userId)

    // Cleanup
    await admin.from('withdrawals').delete().eq('id', body.withdrawal.id)
    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T5.1.4 PASS — Valid withdrawal created with status=pending')
  }, 15_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T5.2 — Admin Approval & Atomic Coin Deduction', () => {

  it('✅ T5.2.1 — PATCH /api/withdrawals: processed status deducts coins atomically', async () => {
    const INITIAL_COINS = 10000
    const COINS_TO_DEDUCT = 5000
    const { userId } = await createTestUser(INITIAL_COINS)

    // Create withdrawal in DB directly
    const { data: wd } = await admin.from('withdrawals').insert({
      user_id: userId,
      user_name: 'QA Atomic',
      user_email: 'atomic@test.com',
      user_mobile: '9000000000',
      amount_inr: 500,
      coins_deducted: COINS_TO_DEDUCT,
      payment_method: 'UPI',
      payout_details: 'atomic@upi',
      status: 'pending',
    }).select('id').single()

    // Admin approves
    const res = await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: wd!.id, status: 'processed' }),
    })
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.success).toBe(true)

    // ⚡ CRITICAL: Verify coins were deducted
    const { data: profile } = await admin
      .from('profiles').select('total_coins, redeemed_coins').eq('id', userId).single()

    expect(profile!.total_coins).toBe(INITIAL_COINS - COINS_TO_DEDUCT)
    expect(profile!.redeemed_coins).toBe(COINS_TO_DEDUCT)

    // Cleanup
    await admin.from('withdrawals').delete().eq('id', wd!.id)
    await admin.auth.admin.deleteUser(userId)
    console.log(`✅ T5.2.1 PASS — Coins atomically deducted: ${INITIAL_COINS} → ${INITIAL_COINS - COINS_TO_DEDUCT}`)
  }, 15_000)

  it('✅ T5.2.2 — Processing same withdrawal twice does NOT double-deduct coins', async () => {
    const INITIAL_COINS = 10000
    const COINS_TO_DEDUCT = 5000
    const { userId } = await createTestUser(INITIAL_COINS)

    const { data: wd } = await admin.from('withdrawals').insert({
      user_id: userId,
      user_name: 'QA IdempW',
      user_email: 'idemp@test.com',
      user_mobile: '9000000000',
      amount_inr: 500,
      coins_deducted: COINS_TO_DEDUCT,
      payment_method: 'UPI',
      payout_details: 'idemp@upi',
      status: 'pending',
    }).select('id').single()

    // First approval
    await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: wd!.id, status: 'processed' }),
    })

    // Second "approval" (re-run) — should NOT double-deduct
    await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: wd!.id, status: 'processed' }),
    })

    // Coins should still be INITIAL - DEDUCT (not INITIAL - 2*DEDUCT)
    const { data: profile } = await admin
      .from('profiles').select('total_coins').eq('id', userId).single()

    const expected = INITIAL_COINS - COINS_TO_DEDUCT
    expect(profile!.total_coins).toBeGreaterThanOrEqual(expected)
    // Should NOT be 0 (double deducted)
    expect(profile!.total_coins).not.toBe(0)

    await admin.from('withdrawals').delete().eq('id', wd!.id)
    await admin.auth.admin.deleteUser(userId)
    console.log(`✅ T5.2.2 PASS — No double deduction (coins=${profile!.total_coins}, expected≥${expected})`)
  }, 20_000)

  it('✅ T5.2.3 — Coin balance cannot go below 0 (Math.max guard)', async () => {
    const INITIAL_COINS = 3000
    const { userId } = await createTestUser(INITIAL_COINS)

    // Create withdrawal requesting MORE coins than user has
    const { data: wd } = await admin.from('withdrawals').insert({
      user_id: userId,
      user_name: 'QA NegTest',
      user_email: 'neg@test.com',
      user_mobile: '9000000000',
      amount_inr: 500,
      coins_deducted: 9999, // More than user has!
      payment_method: 'UPI',
      payout_details: 'neg@upi',
      status: 'pending',
    }).select('id').single()

    await fetch(`${BASE_URL}/api/withdrawals`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: wd!.id, status: 'processed' }),
    })

    const { data: profile } = await admin
      .from('profiles').select('total_coins').eq('id', userId).single()

    // Math.max(0, ...) ensures no negative balance
    expect(profile!.total_coins).toBeGreaterThanOrEqual(0)

    await admin.from('withdrawals').delete().eq('id', wd!.id)
    await admin.auth.admin.deleteUser(userId)
    console.log(`✅ T5.2.3 PASS — Balance floored at 0 (was ${profile!.total_coins})`)
  }, 15_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T5.3 — Withdrawal Stats & Filtering', () => {

  it('✅ T5.3.1 — GET /api/withdrawals returns stats (pendingCount, processedCount)', async () => {
    const res = await fetch(`${BASE_URL}/api/withdrawals`)
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body).toHaveProperty('withdrawals')
    expect(body).toHaveProperty('stats')
    expect(body.stats).toHaveProperty('pendingCount')
    expect(body.stats).toHaveProperty('processedCount')
    expect(body.stats).toHaveProperty('totalRequests')
    console.log(`✅ T5.3.1 PASS — Stats: pending=${body.stats.pendingCount}, processed=${body.stats.processedCount}`)
  }, 10_000)

  it('✅ T5.3.2 — GET /api/withdrawals?status=pending filters correctly', async () => {
    const res = await fetch(`${BASE_URL}/api/withdrawals?status=pending`)
    expect(res.status).toBe(200)
    const body = await res.json()
    const allPending = (body.withdrawals as any[]).every(w => w.status === 'pending')
    expect(allPending).toBe(true)
    console.log(`✅ T5.3.2 PASS — Filter status=pending works (${body.withdrawals.length} records)`)
  }, 10_000)
})
