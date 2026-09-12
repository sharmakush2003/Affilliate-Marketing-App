import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

// 🔒 SECURITY: Never hardcode secrets — must be in .env.local / Vercel env vars
const CUELINKS_API_KEY = process.env.CUELINKS_API_KEY
const CUELINKS_BASE_URL = process.env.CUELINKS_BASE_URL || 'https://www.cuelinks.com/api/v2'
const ADMIN_SECRET = process.env.ADMIN_SECRET

type CueLinksTransaction = {
  id: number | string
  campaign_name: string
  merchant_name?: string
  order_amount: number | string
  commission_earned?: number | string
  payout?: number | string
  status: string
  sub_id: string
  click_time?: string
  transaction_date?: string
  created_at?: string
}

function normalizeCLStatus(status: string): 'pending' | 'approved' | 'rejected' {
  const s = status.toLowerCase()
  if (s === 'approved' || s === 'confirmed' || s === 'validated') return 'approved'
  if (s === 'rejected' || s === 'cancelled' || s === 'invalid') return 'rejected'
  return 'pending'
}

function coinsFromCommission(commission: number): number {
  // 1 Rupee commission = 10 coins (match your app's reward logic)
  return Math.floor(commission * 10)
}

export async function GET(req: NextRequest) {
  // Guard: env vars must be set
  if (!CUELINKS_API_KEY) {
    return NextResponse.json({ error: 'Server misconfiguration: CUELINKS_API_KEY not set in environment variables.' }, { status: 500 })
  }
  if (!ADMIN_SECRET) {
    return NextResponse.json({ error: 'Server misconfiguration: ADMIN_SECRET not set in environment variables.' }, { status: 500 })
  }

  // Validate secret to prevent unauthorized triggers
  const secret = req.headers.get('x-admin-secret') || req.nextUrl.searchParams.get('secret')
  if (secret !== ADMIN_SECRET) {
    return NextResponse.json({ error: 'Unauthorized' }, { status: 401 })
  }

  try {
    // ── 1. Fetch latest transactions from CueLinks API ──────────────────────
    const clUrl = `${CUELINKS_BASE_URL}/transactions.json`
    const clRes = await fetch(clUrl, {
      headers: {
        Authorization: `Bearer ${CUELINKS_API_KEY}`,
        'Content-Type': 'application/json',
      },
      cache: 'no-store',
    })

    if (clRes.status === 204) {
      return NextResponse.json({ message: 'No CueLinks transactions found (204 No Content)', synced: 0, coinsAwarded: 0 })
    }

    if (!clRes.ok) {
      const errText = await clRes.text()
      return NextResponse.json(
        { error: `CueLinks API error ${clRes.status}: ${errText}` },
        { status: 502 }
      )
    }

    const clData = await clRes.json()
    const clTransactions: CueLinksTransaction[] = Array.isArray(clData)
      ? clData
      : clData?.transactions ?? clData?.data ?? []

    if (clTransactions.length === 0) {
      return NextResponse.json({ message: 'No CueLinks transactions found', synced: 0 })
    }

    let synced = 0
    let coinsAwarded = 0
    const errors: string[] = []

    // ── 2. Upsert each transaction into Supabase ─────────────────────────────
    for (const cl of clTransactions) {
      const userId = cl.sub_id?.trim()
      const transactionId = String(cl.id)
      const commission = parseFloat(String(cl.commission_earned ?? cl.payout ?? 0))
      const normalizedStatus = normalizeCLStatus(cl.status)
      const orderAmount = parseFloat(String(cl.order_amount ?? 0))

      // ── 3. Check if profile exists for this user_id ─────────────────────
      let resolvedUserId: string | null = null
      if (userId && userId.length > 10) {
        const { data: profileData } = await supabaseAdmin
          .from('profiles')
          .select('id')
          .eq('id', userId)
          .single()
        if (profileData) resolvedUserId = profileData.id
      }

      const upsertPayload = {
        cuelinks_transaction_id: transactionId,
        user_id: resolvedUserId,
        campaign_name: cl.campaign_name ?? 'Unknown Campaign',
        merchant_name: cl.merchant_name ?? null,
        order_amount: orderAmount,
        commission_earned: commission,
        status: normalizedStatus,
        click_time: cl.click_time ?? null,
        transaction_time: cl.transaction_date ?? cl.created_at ?? null,
      }

      const { data: upserted, error: upsertErr } = await supabaseAdmin
        .from('transactions')
        .upsert(upsertPayload, { onConflict: 'cuelinks_transaction_id', ignoreDuplicates: false })
        .select()
        .single()

      if (upsertErr) {
        errors.push(`TX ${transactionId}: ${upsertErr.message}`)
        continue
      }

      synced++

      // ── 4. If approved + NOT YET CREDITED + has a valid user → credit coins ─
      // Guard uses cashback_credited_coins === 0 (immutable once set) to prevent
      // double-crediting if sync runs twice before admin marks as paid.
      if (
        normalizedStatus === 'approved' &&
        (upserted?.cashback_credited_coins ?? 0) === 0 &&
        resolvedUserId &&
        commission > 0
      ) {
        const coins = coinsFromCommission(commission)

        // Fetch current profile coins
        const { data: profile } = await supabaseAdmin
          .from('profiles')
          .select('total_coins, total_savings')
          .eq('id', resolvedUserId)
          .single()

        if (profile) {
          const newCoins = (profile.total_coins ?? 0) + coins
          const newSavings = (profile.total_savings ?? 0) + Math.floor(commission)

          await supabaseAdmin
            .from('profiles')
            .update({ total_coins: newCoins, total_savings: newSavings })
            .eq('id', resolvedUserId)

          // Mark transaction as credited (update cashback_credited_coins)
          await supabaseAdmin
            .from('transactions')
            .update({ cashback_credited_coins: coins })
            .eq('cuelinks_transaction_id', transactionId)

          coinsAwarded += coins
        }
      }
    }

    return NextResponse.json({
      message: 'CueLinks sync complete',
      synced,
      coinsAwarded,
      errors: errors.length > 0 ? errors : undefined,
    })
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : 'Unknown error'
    console.error('[sync-cuelinks] Error:', msg)
    return NextResponse.json({ error: msg }, { status: 500 })
  }
}
