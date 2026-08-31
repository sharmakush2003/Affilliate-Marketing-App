import { NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function GET() {
  // Fetch all KPIs in parallel safely
  let totalClicksCount: number | null = 0
  let recentClicksData: any[] | null = []

  try {
    const clicksRes = await supabaseAdmin.from('clicks').select('*', { count: 'exact', head: true })
    totalClicksCount = clicksRes.count ?? 0
  } catch (e) {
    totalClicksCount = 0
  }

  try {
    const [clicksListRes, profilesRes] = await Promise.all([
      supabaseAdmin.from('clicks').select('id, brand_name, click_time, user_id').order('click_time', { ascending: false }).limit(5),
      supabaseAdmin.from('profiles').select('id, full_name, email')
    ])
    const profileMap = new Map((profilesRes.data ?? []).map((p: any) => [p.id, p]))
    recentClicksData = (clicksListRes.data ?? []).map((c: any) => {
      const prof = profileMap.get(c.user_id)
      return {
        id: c.id,
        campaign_name: c.brand_name || 'Affiliate Campaign',
        created_at: c.click_time || new Date().toISOString(),
        sub_id: c.user_id ? String(c.user_id).slice(0, 16) + '...' : 'SUB_API',
        destination_url: `https://linksredirect.com/?cid=301603&url=${encodeURIComponent('https://' + String(c.brand_name || 'store').toLowerCase().replace(/\s+/g, '') + '.com')}`,
        profiles: prof ? { full_name: prof.full_name, email: prof.email } : null
      }
    })
  } catch (e) {
    recentClicksData = []
  }

  const [
    { count: totalUsers },
    { data: transactionStats },
    { data: pendingStats },
    { data: recentUsers },
    { data: recentTransactions },
  ] = await Promise.all([
    supabaseAdmin.from('profiles').select('*', { count: 'exact', head: true }),
    supabaseAdmin
      .from('transactions')
      .select('order_amount, commission_earned, status, payment_status, cashback_credited_coins'),
    supabaseAdmin
      .from('transactions')
      .select('commission_earned')
      .eq('payment_status', 'unpaid')
      .eq('status', 'approved'),
    supabaseAdmin
      .from('profiles')
      .select('id, full_name, email, total_coins, created_at')
      .order('created_at', { ascending: false })
      .limit(5),
    supabaseAdmin
      .from('transactions')
      .select('id, merchant_name, order_amount, commission_earned, status, transaction_time, profiles(full_name, email)')
      .order('created_at', { ascending: false })
      .limit(5),
  ])

  const allTransactions = transactionStats ?? []
  const totalOrderVolume = allTransactions.reduce((sum: number, t: any) => sum + (t.order_amount ?? 0), 0)
  const totalCommission = allTransactions.reduce((sum: number, t: any) => sum + (t.commission_earned ?? 0), 0)
  const totalCashbackCoins = allTransactions.reduce((sum: number, t: any) => sum + (t.cashback_credited_coins ?? 0), 0)
  const totalTransactions = allTransactions.length
  const approvedCount = allTransactions.filter((t: any) => t.status === 'approved').length
  const pendingCount = allTransactions.filter((t: any) => t.status === 'pending').length
  const rejectedCount = allTransactions.filter((t: any) => t.status === 'rejected').length
  const totalUnpaidCommission = (pendingStats ?? []).reduce((sum: number, t: any) => sum + (t.commission_earned ?? 0), 0)

  // Return actual live count from Supabase click_logs
  const finalClicks = totalClicksCount ?? 0

  return NextResponse.json({
    kpis: {
      totalUsers: totalUsers ?? 0,
      totalClicks: finalClicks,
      totalOrderVolume: totalOrderVolume.toFixed(2),
      totalCommission: totalCommission.toFixed(2),
      totalCashbackCoins,
      totalTransactions,
      approvedCount,
      pendingCount,
      rejectedCount,
      totalUnpaidCommission: totalUnpaidCommission.toFixed(2),
    },
    recentUsers: recentUsers ?? [],
    recentTransactions: recentTransactions ?? [],
    recentClicks: recentClicksData ?? [],
  })
}

