import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

// GET: Fetch paginated users
export async function GET(req: NextRequest) {
  const { searchParams } = req.nextUrl
  const page = parseInt(searchParams.get('page') ?? '1')
  const limit = parseInt(searchParams.get('limit') ?? '20')
  const search = searchParams.get('search') ?? ''
  const from = (page - 1) * limit
  const to = from + limit - 1

  let query = supabaseAdmin
    .from('profiles')
    .select('*', { count: 'exact' })
    .order('created_at', { ascending: false })
    .range(from, to)

  if (search) {
    query = query.or(`email.ilike.%${search}%,full_name.ilike.%${search}%`)
  }

  const { data, error, count } = await query

  if (error) return NextResponse.json({ error: error.message }, { status: 500 })
  return NextResponse.json({ users: data, total: count, page, limit })
}

// PATCH: Adjust coins/savings for a specific user
export async function PATCH(req: NextRequest) {
  const { userId, coinsDelta, savingsDelta, reason } = await req.json()

  if (!userId) return NextResponse.json({ error: 'userId is required' }, { status: 400 })

  const { data: profile, error: fetchErr } = await supabaseAdmin
    .from('profiles')
    .select('total_coins, redeemed_coins, total_savings')
    .eq('id', userId)
    .single()

  if (fetchErr || !profile) return NextResponse.json({ error: 'User not found' }, { status: 404 })

  const updatedCoins = Math.max(0, (profile.total_coins ?? 0) + (coinsDelta ?? 0))
  const updatedSavings = Math.max(0, (profile.total_savings ?? 0) + (savingsDelta ?? 0))

  const { data, error } = await supabaseAdmin
    .from('profiles')
    .update({ total_coins: updatedCoins, total_savings: updatedSavings })
    .eq('id', userId)
    .select()
    .single()

  if (error) return NextResponse.json({ error: error.message }, { status: 500 })

  console.log(`[admin] Coins adjusted for ${userId}: delta=${coinsDelta}, reason=${reason}`)
  return NextResponse.json({ success: true, user: data })
}
