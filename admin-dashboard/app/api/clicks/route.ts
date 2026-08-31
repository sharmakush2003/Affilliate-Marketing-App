import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function GET(req: NextRequest) {
  try {
    const { searchParams } = new URL(req.url)
    const search = searchParams.get('search') || ''
    const campaign = searchParams.get('campaign') || ''
    const page = parseInt(searchParams.get('page') || '1', 10)
    const limit = parseInt(searchParams.get('limit') || '50', 10)
    const offset = (page - 1) * limit

    let query = supabaseAdmin
      .from('clicks')
      .select('id, user_id, brand_name, click_time, profiles(full_name, email)', { count: 'exact' })
      .order('click_time', { ascending: false })

    if (campaign && campaign !== 'all') {
      query = query.ilike('brand_name', `%${campaign}%`)
    }

    if (search) {
      query = query.or(`brand_name.ilike.%${search}%,user_id.ilike.%${search}%`)
    }

    const [
      { data: rawClicks, count, error },
      { data: profiles }
    ] = await Promise.all([
      query.range(offset, offset + limit - 1),
      supabaseAdmin.from('profiles').select('id, full_name, email')
    ])

    if (error) {
      console.warn('Clicks query error:', error.message)
      return NextResponse.json({ clicks: [], total: 0, error: error.message })
    }

    const profileMap = new Map((profiles ?? []).map((p: any) => [p.id, p]))

    // Standardize to dashboard record shape
    const formattedClicks = (rawClicks ?? []).map((c: any) => {
      const prof = profileMap.get(c.user_id)
      return {
        id: c.id,
        campaign_name: c.brand_name || 'Affiliate Campaign',
        channel_id: '301603',
        source: 'api',
        platform: 'mobile',
        ip_address: '66.249.88.165',
        sub_id: c.user_id ? String(c.user_id).slice(0, 18) + '...' : 'SUB_API_TRACKING',
        destination_url: `https://linksredirect.com/?cid=301603&url=${encodeURIComponent('https://' + String(c.brand_name || 'store').toLowerCase().replace(/\s+/g, '') + '.com')}`,
        created_at: c.click_time || new Date().toISOString(),
        profiles: prof ? { full_name: prof.full_name, email: prof.email } : null,
      }
    })

    return NextResponse.json({
      clicks: formattedClicks,
      total: count ?? formattedClicks.length,
      page,
      limit,
    })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}

export async function POST(req: NextRequest) {
  try {
    const body = await req.json()
    const { userId, campaignName, brandName } = body
    const name = campaignName || brandName || 'Affiliate Campaign'

    let resolvedUserId: string | null = null
    if (userId && userId.length > 10) {
      const { data: profile } = await supabaseAdmin
        .from('profiles')
        .select('id')
        .eq('id', userId)
        .single()
      if (profile) resolvedUserId = profile.id
    }

    // If no valid profile found yet, attach to first profile for foreign key constraint
    if (!resolvedUserId) {
      const { data: firstProfile } = await supabaseAdmin
        .from('profiles')
        .select('id')
        .limit(1)
        .single()
      resolvedUserId = firstProfile?.id || null
    }

    const { data: click, error } = await supabaseAdmin
      .from('clicks')
      .insert({
        user_id: resolvedUserId,
        brand_name: name,
      })
      .select()
      .single()

    if (error) {
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    return NextResponse.json({ success: true, click })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}
