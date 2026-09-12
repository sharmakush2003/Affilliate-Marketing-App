import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export type WithdrawalRecord = {
  id: string
  user_id: string
  user_name: string
  user_email: string
  user_mobile: string
  amount_inr: number
  coins_deducted: number
  payment_method: string
  payout_details: string
  status: 'pending' | 'approved' | 'processed' | 'rejected'
  created_at: string
  processed_at?: string | null
  tx_hash_or_ref?: string | null
}

// ── Minimum withdrawal threshold ─────────────────────────────────────────────
const MIN_COINS = 5000   // 5,000 coins = ₹500
const MIN_INR   = 500

export async function GET(req: NextRequest) {
  try {
    const { searchParams } = new URL(req.url)
    const status = searchParams.get('status') || 'all'
    const search = (searchParams.get('search') || '').toLowerCase().trim()

    // ✅ Always fetch from Supabase — in-memory state does NOT persist on Vercel serverless
    const { data, error } = await supabaseAdmin
      .from('withdrawals')
      .select('*')
      .order('created_at', { ascending: false })

    if (error) {
      console.error('[withdrawals] fetch error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    let records: WithdrawalRecord[] = data ?? []

    // Filter by status
    let filtered = status !== 'all' ? records.filter((r) => r.status === status) : records

    // Filter by search
    if (search) {
      filtered = filtered.filter(
        (r) =>
          (r.user_name || '').toLowerCase().includes(search) ||
          (r.user_email || '').toLowerCase().includes(search) ||
          (r.payout_details || '').toLowerCase().includes(search)
      )
    }

    const pendingTotal   = records.filter((r) => r.status === 'pending').reduce((s, r) => s + r.amount_inr, 0)
    const processedTotal = records.filter((r) => r.status === 'processed').reduce((s, r) => s + r.amount_inr, 0)

    return NextResponse.json({
      withdrawals: filtered,
      total: filtered.length,
      stats: {
        pendingCount:    records.filter((r) => r.status === 'pending').length,
        pendingAmount:   pendingTotal,
        processedCount:  records.filter((r) => r.status === 'processed').length,
        processedAmount: processedTotal,
        totalRequests:   records.length,
      },
    })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}

export async function POST(req: NextRequest) {
  try {
    const body = await req.json()
    const { userId, amountInr, coinsDeducted, paymentMethod, payoutDetails, userName, userEmail, userMobile } = body

    // ── Validate minimum threshold ────────────────────────────────────────────
    if (!amountInr || amountInr < MIN_INR) {
      return NextResponse.json(
        { error: `Minimum withdrawal amount is ₹${MIN_INR} (${MIN_COINS} coins).` },
        { status: 400 }
      )
    }
    if (!coinsDeducted || coinsDeducted < MIN_COINS) {
      return NextResponse.json(
        { error: `Minimum ${MIN_COINS} coins required to withdraw.` },
        { status: 400 }
      )
    }
    if (!userId || !paymentMethod || !payoutDetails) {
      return NextResponse.json({ error: 'userId, paymentMethod and payoutDetails are required.' }, { status: 400 })
    }

    // ── Check user has enough coins ───────────────────────────────────────────
    const { data: profile, error: profileErr } = await supabaseAdmin
      .from('profiles')
      .select('total_coins, redeemed_coins')
      .eq('id', userId)
      .single()

    if (profileErr || !profile) {
      return NextResponse.json({ error: 'User profile not found.' }, { status: 404 })
    }

    if ((profile.total_coins ?? 0) < coinsDeducted) {
      return NextResponse.json({ error: 'Insufficient coin balance.' }, { status: 400 })
    }

    // ── Insert withdrawal record ──────────────────────────────────────────────
    const { data: withdrawal, error: insertErr } = await supabaseAdmin
      .from('withdrawals')
      .insert({
        user_id:        userId,
        user_name:      userName || '',
        user_email:     userEmail || '',
        user_mobile:    userMobile || '',
        amount_inr:     amountInr,
        coins_deducted: coinsDeducted,
        payment_method: paymentMethod,
        payout_details: payoutDetails,
        status:         'pending',
      })
      .select()
      .single()

    if (insertErr) {
      return NextResponse.json({ error: insertErr.message }, { status: 500 })
    }

    return NextResponse.json({ success: true, withdrawal })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}

export async function PATCH(req: NextRequest) {
  try {
    const body = await req.json()
    const { id, status, txHash } = body

    if (!id || !status) {
      return NextResponse.json({ error: 'Missing id or status' }, { status: 400 })
    }

    // ── Fetch withdrawal to get user_id and coins_deducted ───────────────────
    const { data: withdrawal, error: fetchErr } = await supabaseAdmin
      .from('withdrawals')
      .select('user_id, coins_deducted, status')
      .eq('id', id)
      .single()

    if (fetchErr || !withdrawal) {
      return NextResponse.json({ error: 'Withdrawal not found.' }, { status: 404 })
    }

    // ── Update withdrawal status ──────────────────────────────────────────────
    const { error: updateErr } = await supabaseAdmin
      .from('withdrawals')
      .update({
        status,
        tx_hash_or_ref: txHash || null,
        processed_at: status === 'processed' ? new Date().toISOString() : null,
      })
      .eq('id', id)

    if (updateErr) {
      return NextResponse.json({ error: updateErr.message }, { status: 500 })
    }

    // ── 💰 Atomic coin deduction when payout is processed ────────────────────
    // Only deduct when transitioning TO 'processed' (not on re-update)
    if (status === 'processed' && withdrawal.status !== 'processed' && withdrawal.user_id && withdrawal.coins_deducted > 0) {
      const { data: profile } = await supabaseAdmin
        .from('profiles')
        .select('total_coins, redeemed_coins')
        .eq('id', withdrawal.user_id)
        .single()

      if (profile) {
        const newTotalCoins    = Math.max(0, (profile.total_coins ?? 0) - withdrawal.coins_deducted)
        const newRedeemedCoins = (profile.redeemed_coins ?? 0) + withdrawal.coins_deducted

        await supabaseAdmin
          .from('profiles')
          .update({ total_coins: newTotalCoins, redeemed_coins: newRedeemedCoins })
          .eq('id', withdrawal.user_id)
      }
    }

    return NextResponse.json({ success: true, message: `Payout status updated to ${status}` })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}
