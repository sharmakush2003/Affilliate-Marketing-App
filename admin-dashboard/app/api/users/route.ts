import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

// GET: Fetch paginated users (with auto-sync from auth.users to profiles)
export async function GET(req: NextRequest) {
  try {
    const { searchParams } = req.nextUrl
    const page = parseInt(searchParams.get('page') ?? '1')
    const limit = parseInt(searchParams.get('limit') ?? '50')
    const search = searchParams.get('search') ?? ''
    const from = (page - 1) * limit
    const to = from + limit - 1

    // 1. Auto-sync users from auth.users to public.profiles table
    try {
      const { data: authData } = await supabaseAdmin.auth.admin.listUsers()
      if (authData?.users?.length) {
        const { data: existingProfiles } = await supabaseAdmin.from('profiles').select('id')
        const existingIds = new Set(existingProfiles?.map(p => p.id) ?? [])

        const missingProfiles = authData.users
          .filter(u => !existingIds.has(u.id))
          .map(u => ({
            id: u.id,
            email: u.email ?? '',
            full_name: (u.user_metadata?.full_name || u.user_metadata?.display_name || u.user_metadata?.name || u.email?.split('@')[0] || 'User'),
            mobile: (u.user_metadata?.mobile || u.user_metadata?.phone || ''),
            total_coins: 0,
            redeemed_coins: 0,
            total_savings: 0,
            created_at: u.created_at,
          }))

        if (missingProfiles.length > 0) {
          await supabaseAdmin.from('profiles').upsert(missingProfiles)
        }
      }
    } catch (syncErr) {
      console.warn('[api/users] Sync warning:', syncErr)
    }

    // 2. Fetch profiles
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
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Server error' }, { status: 500 })
  }
}

// POST: Upsert profile (used by app or server to record name/mobile/email)
export async function POST(req: NextRequest) {
  try {
    const { id, email, fullName, mobile } = await req.json()

    if (!id || !email) {
      return NextResponse.json({ error: 'id and email are required' }, { status: 400 })
    }

    const { data, error } = await supabaseAdmin
      .from('profiles')
      .upsert({
        id,
        email,
        full_name: fullName || email.split('@')[0],
        mobile: mobile || '',
      })
      .select()
      .single()

    if (error) return NextResponse.json({ error: error.message }, { status: 500 })
    return NextResponse.json({ success: true, profile: data })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Server error' }, { status: 500 })
  }
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
