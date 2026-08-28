import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function GET(req: NextRequest) {
  try {
    const { searchParams } = new URL(req.url)
    const search = searchParams.get('search') || ''
    const campaign = searchParams.get('campaign') || ''
    const page = parseInt(searchParams.get('page') || '1', 10)
    const limit = parseInt(searchParams.get('limit') || '20', 10)
    const offset = (page - 1) * limit

    let query = supabaseAdmin
      .from('click_logs')
      .select('*, profiles(full_name, email)', { count: 'exact' })
      .order('created_at', { ascending: false })

    if (campaign && campaign !== 'all') {
      query = query.eq('campaign_name', campaign)
    }

    if (search) {
      query = query.or(`campaign_name.ilike.%${search}%,sub_id.ilike.%${search}%,destination_url.ilike.%${search}%`)
    }

    const { data: clicks, count, error } = await query.range(offset, offset + limit - 1)

    if (error) {
      // If table doesn't exist yet in Supabase or fails, return empty gracefully
      console.warn('Click logs query error:', error.message)
      return NextResponse.json({ clicks: [], total: 0, error: error.message })
    }

    return NextResponse.json({
      clicks: clicks ?? [],
      total: count ?? 0,
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
    const { userId, subId, campaignName, destinationUrl, channelId, source, platform, ipAddress } = body

    if (!campaignName) {
      return NextResponse.json({ error: 'campaignName is required' }, { status: 400 })
    }

    let resolvedUserId: string | null = null
    if (userId && userId.length > 10) {
      const { data: profile } = await supabaseAdmin
        .from('profiles')
        .select('id')
        .eq('id', userId)
        .single()
      if (profile) resolvedUserId = profile.id
    }

    const { data: click, error } = await supabaseAdmin
      .from('click_logs')
      .insert({
        user_id: resolvedUserId,
        sub_id: subId || userId || null,
        campaign_name: campaignName,
        destination_url: destinationUrl || null,
        channel_id: channelId || '301603',
        source: source || 'api',
        platform: platform || 'mobile',
        ip_address: ipAddress || '66.249.88.165',
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
