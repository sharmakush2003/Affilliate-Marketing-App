import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

const CUELINKS_API_KEY = process.env.CUELINKS_API_KEY!
const CUELINKS_BASE_URL = process.env.CUELINKS_BASE_URL || 'https://www.cuelinks.com/api/v2'

export async function GET(req: NextRequest) {
  try {
    const { searchParams } = new URL(req.url)
    const search = searchParams.get('search')?.toLowerCase() || ''
    const category = searchParams.get('category')?.toLowerCase() || 'all'

    // 1. Fetch live offers directly from CueLinks API v2
    const clUrl = `${CUELINKS_BASE_URL}/offers.json`
    const clRes = await fetch(clUrl, {
      headers: {
        Authorization: `Bearer ${CUELINKS_API_KEY}`,
        'Content-Type': 'application/json',
      },
      next: { revalidate: 300 }, // 5 min cache for super-fast loads
    })

    let rawOffers: any[] = []
    if (clRes.ok) {
      const data = await clRes.json()
      rawOffers = Array.isArray(data) ? data : data?.offers ?? data?.data ?? []
    }

    // Standardize CueLinks offer format
    let formattedOffers = rawOffers.map((o: any) => {
      const catObj = o.categories || {}
      const catVal = Object.values(catObj)[0] as string || 'General'
      
      return {
        id: o.id || Math.random().toString(),
        cuelinks_offer_id: o.id,
        campaign_name: o.campaign || o.merchant || 'General Merchant',
        merchant_name: o.campaign || o.merchant || 'Store',
        title: o.title || 'Special Promotion Deal',
        description: o.description ? o.description.replace(/<[^>]*>?/gm, '') : '',
        coupon_code: o.coupon_code && o.coupon_code.trim() ? o.coupon_code.trim() : 'No Coupon Required',
        category: catVal,
        image_url: o.image_url && !o.image_url.includes('missing.png') ? o.image_url : null,
        target_url: o.url || 'https://www.cuelinks.com',
        affiliate_url: o.affiliate_url || o.url || '#',
        valid_till: o.end_date || '2027-12-31',
        type: o.type || 'discount',
        status: o.status || 'live'
      }
    })

    // Filter by search & category
    if (search) {
      formattedOffers = formattedOffers.filter(
        (o) =>
          o.title.toLowerCase().includes(search) ||
          o.campaign_name.toLowerCase().includes(search) ||
          o.coupon_code.toLowerCase().includes(search)
      )
    }

    if (category !== 'all') {
      formattedOffers = formattedOffers.filter((o) =>
        o.category.toLowerCase().includes(category)
      )
    }

    return NextResponse.json({
      offers: formattedOffers,
      total: formattedOffers.length,
      source: clRes.ok ? 'cuelinks_live_api' : 'fallback'
    })
  } catch (err: any) {
    console.error('Error fetching offers:', err)
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}
