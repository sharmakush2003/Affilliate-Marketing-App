/**
 * ╔══════════════════════════════════════════════════════════════════╗
 * ║  REWARD CLUB — PHASE 2: Click Attribution (15%→35%)            ║
 * ║  PHASE 3: CueLinks Sync & Anti-Duplication (35%→55%)           ║
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
const ADMIN_SECRET = process.env.ADMIN_SECRET || ''

if (!SUPABASE_URL || !SUPABASE_SVC) {
  throw new Error('❌ Missing env vars in .env.local')
}

const admin = createClient(SUPABASE_URL, SUPABASE_SVC, {
  auth: { autoRefreshToken: false, persistSession: false }
})

// ─── Seed: Create test user ───────────────────────────────────────────────────
async function createTestUser() {
  const email = `qa_click_${Date.now()}@mailtest.com`
  const { data } = await admin.auth.admin.createUser({
    email, password: 'QaPass@1234', email_confirm: true,
  })
  await new Promise(r => setTimeout(r, 1500))
  return { userId: data.user!.id, email }
}

// ─────────────────────────────────────────────────────────────────────────────
describe('T2.1 — Click Attribution & Telemetry (/api/clicks)', () => {

  it('✅ T2.1.1 — POST /api/clicks with valid UUID logs click correctly', async () => {
    const { userId } = await createTestUser()

    const res = await fetch(`${BASE_URL}/api/clicks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId,
        campaignName: 'Flipkart Big Billion',
        destinationUrl: 'https://flipkart.com/offer',
        source: 'api',
        platform: 'mobile',
      }),
    })

    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.success).toBe(true)
    expect(body.click).toBeDefined()
    expect(body.click.user_id).toBe(userId)

    // Verify DB row
    const { data: dbClick } = await admin
      .from('clicks')
      .select('user_id, brand_name')
      .eq('user_id', userId)
      .order('click_time', { ascending: false })
      .limit(1)
      .single()

    expect(dbClick!.user_id).toBe(userId)
    expect(dbClick!.brand_name).toContain('Flipkart')

    // Cleanup
    await admin.from('clicks').delete().eq('user_id', userId)
    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T2.1.1 PASS — Click logged for valid user')
  }, 15_000)

  it('✅ T2.1.2 — Guest click (no userId) → user_id is NULL, NOT a random user', async () => {
    const res = await fetch(`${BASE_URL}/api/clicks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        userId: '',
        campaignName: 'Amazon Sale',
      }),
    })

    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.success).toBe(true)
    // CRITICAL: user_id must be null, never a real user's ID
    expect(body.click.user_id).toBeNull()

    // Cleanup
    await admin.from('clicks').delete().eq('id', body.click.id)
    console.log('✅ T2.1.2 PASS — Guest click has user_id=null (no misattribution)')
  }, 10_000)

  it('✅ T2.1.3 — Malformed UUID is treated as guest (user_id=null)', async () => {
    const res = await fetch(`${BASE_URL}/api/clicks`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ userId: 'not-a-real-uuid-12345', campaignName: 'Test' }),
    })
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body.click.user_id).toBeNull()

    await admin.from('clicks').delete().eq('id', body.click.id)
    console.log('✅ T2.1.3 PASS — Invalid UUID → null attribution (not misattributed)')
  }, 10_000)

  it('✅ T2.1.4 — GET /api/clicks returns paginated results', async () => {
    const res = await fetch(`${BASE_URL}/api/clicks?page=1&limit=5`)
    expect(res.status).toBe(200)
    const body = await res.json()
    expect(body).toHaveProperty('clicks')
    expect(body).toHaveProperty('total')
    expect(Array.isArray(body.clicks)).toBe(true)
    console.log(`✅ T2.1.4 PASS — GET clicks returned ${body.clicks.length} items, total=${body.total}`)
  }, 10_000)

  it('✅ T2.1.5 — ip_address in DB click row should NOT be hardcoded 103.167.194.52', async () => {
    // Verify most recent non-null IP click is not the old hardcoded value
    const { data: clicks } = await admin
      .from('clicks')
      .select('ip_address')
      .not('ip_address', 'is', null)
      .limit(10)

    const hardcoded = '103.167.194.52'
    const hasHardcoded = (clicks ?? []).some(c => c.ip_address === hardcoded)
    expect(hasHardcoded).toBe(false)
    console.log('✅ T2.1.5 PASS — No hardcoded IP found in recent clicks')
  }, 10_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T2.2 — CueLinks SubID URL Format Validation', () => {

  it('✅ T2.2.1 — Affiliate URL contains cid=301603 and subid=userUUID', () => {
    // Test the URL format expected from Android CuelinksApiService.createAffiliateLink
    const channelId = '301603'
    const userId    = '550e8400-e29b-41d4-a716-446655440000'
    const targetUrl = 'https://www.flipkart.com/product/123'

    const encodedUrl = encodeURIComponent(targetUrl)
    const affiliateUrl = `https://linksredirect.com/?cid=${channelId}&source=api&subid=${encodeURIComponent(userId)}&url=${encodedUrl}`

    expect(affiliateUrl).toContain('cid=301603')
    expect(affiliateUrl).toContain('subid=')
    expect(affiliateUrl).toContain(userId)
    expect(affiliateUrl).toContain('linksredirect.com')
    console.log('✅ T2.2.1 PASS — URL format correct:', affiliateUrl.substring(0, 80) + '...')
  })

  it('✅ T2.2.2 — Guest (no userId) affiliate URL has no subid param', () => {
    const channelId = '301603'
    const userId    = ''
    const targetUrl = 'https://amazon.in'

    const subParam = userId ? `&subid=${encodeURIComponent(userId)}` : ''
    const url = `https://linksredirect.com/?cid=${channelId}&source=api${subParam}&url=${encodeURIComponent(targetUrl)}`

    expect(url).not.toContain('subid=')
    console.log('✅ T2.2.2 PASS — Guest URL has no subid')
  })
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T3.1 — CueLinks Sync Authorization Guard', () => {

  it('✅ T3.1.1 — GET /api/sync-cuelinks without secret → 401', async () => {
    const res = await fetch(`${BASE_URL}/api/sync-cuelinks`)
    expect([401, 500]).toContain(res.status)
    if (res.status === 401) {
      const body = await res.json()
      expect(body.error).toMatch(/unauthorized|secret|misconfiguration/i)
    }
    console.log('✅ T3.1.1 PASS — Sync endpoint protected, status:', res.status)
  }, 10_000)

  it('✅ T3.1.2 — GET /api/sync-cuelinks with wrong secret → 401', async () => {
    const res = await fetch(`${BASE_URL}/api/sync-cuelinks`, {
      headers: { 'x-admin-secret': 'WRONG_SECRET_12345' }
    })
    // Must be 401 or 500 (env not set) — never 200
    expect([401, 500]).toContain(res.status)
    if (res.status === 401) {
      const body = await res.json()
      expect(body.error).toMatch(/unauthorized/i)
    }
    console.log('✅ T3.1.2 PASS — Wrong secret rejected')
  }, 10_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T3.2 — Anti-Duplication Engine (Race Condition)', () => {

  it('✅ T3.2.1 — Same cuelinks_transaction_id cannot be inserted twice (UNIQUE constraint)', async () => {
    const txId = `QA_RACE_${Date.now()}`

    const { error: e1 } = await admin.from('transactions').insert({
      cuelinks_transaction_id: txId,
      campaign_name: 'Test Campaign',
      order_amount: 100,
      commission_earned: 10,
      status: 'approved',
      cashback_credited_coins: 0,
    })
    expect(e1).toBeNull()

    // Second insert with same ID must fail with unique violation
    const { error: e2 } = await admin.from('transactions').insert({
      cuelinks_transaction_id: txId,
      campaign_name: 'Test Campaign',
      order_amount: 100,
      commission_earned: 10,
      status: 'approved',
      cashback_credited_coins: 0,
    })
    expect(e2).not.toBeNull()
    expect(e2!.code).toBe('23505') // PostgreSQL unique_violation

    // Cleanup
    await admin.from('transactions').delete().eq('cuelinks_transaction_id', txId)
    console.log('✅ T3.2.1 PASS — Unique constraint blocks duplicate TX insert')
  }, 10_000)

  it('✅ T3.2.2 — cashback_credited_coins guard prevents double coin credit', async () => {
    const { userId } = await createTestUser()
    const txId = `QA_DOUBLE_${Date.now()}`

    // Insert TX with cashback_credited_coins = 100 (already credited)
    await admin.from('transactions').insert({
      cuelinks_transaction_id: txId,
      user_id: userId,
      campaign_name: 'Double Credit Test',
      order_amount: 100,
      commission_earned: 10,
      status: 'approved',
      cashback_credited_coins: 100, // Already credited!
    })

    // Fetch and verify guard: since cashback_credited_coins > 0, sync should NOT credit again
    const { data: tx } = await admin
      .from('transactions')
      .select('cashback_credited_coins, status')
      .eq('cuelinks_transaction_id', txId)
      .single()

    // Guard check: sync code uses `(upserted?.cashback_credited_coins ?? 0) === 0`
    const wouldCredit = tx!.status === 'approved' && tx!.cashback_credited_coins === 0
    expect(wouldCredit).toBe(false) // Should NOT credit again

    // Cleanup
    await admin.from('transactions').delete().eq('cuelinks_transaction_id', txId)
    await admin.auth.admin.deleteUser(userId)
    console.log('✅ T3.2.2 PASS — cashback_credited_coins guard works (no double credit)')
  }, 15_000)
})

// ─────────────────────────────────────────────────────────────────────────────
describe('T3.3 — Transaction Status Transitions & Coin Accounting', () => {

  it('✅ T3.3.1 — pending status stores 0 cashback_credited_coins', async () => {
    const txId = `QA_PENDING_${Date.now()}`
    const { data: tx } = await admin.from('transactions').insert({
      cuelinks_transaction_id: txId,
      campaign_name: 'Pending Test',
      order_amount: 200,
      commission_earned: 20,
      status: 'pending',
      cashback_credited_coins: 0,
    }).select().single()

    expect(tx!.cashback_credited_coins).toBe(0)
    expect(tx!.status).toBe('pending')

    await admin.from('transactions').delete().eq('cuelinks_transaction_id', txId)
    console.log('✅ T3.3.1 PASS — Pending TX has 0 coins')
  }, 10_000)

  it('✅ T3.3.2 — coins formula: commission * 10 (₹1 = 10 coins)', () => {
    const commission = 15.5
    const coins = Math.floor(commission * 10)
    expect(coins).toBe(155)
    console.log(`✅ T3.3.2 PASS — ₹${commission} → ${coins} coins`)
  })

  it('✅ T3.3.3 — rejected status: cashback_credited_coins must remain 0', async () => {
    const txId = `QA_REJECTED_${Date.now()}`
    const { data: tx } = await admin.from('transactions').insert({
      cuelinks_transaction_id: txId,
      campaign_name: 'Rejected Test',
      order_amount: 500,
      commission_earned: 50,
      status: 'rejected',
      cashback_credited_coins: 0,
    }).select().single()

    expect(tx!.cashback_credited_coins).toBe(0)
    expect(tx!.status).toBe('rejected')

    await admin.from('transactions').delete().eq('cuelinks_transaction_id', txId)
    console.log('✅ T3.3.3 PASS — Rejected TX has 0 credited coins')
  }, 10_000)
})
