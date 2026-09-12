import { NextRequest, NextResponse } from 'next/server'
import fs from 'fs'
import path from 'path'

// 🔒 SECURITY: Never hardcode secrets — must be in .env.local / Vercel env vars
const CUELINKS_API_KEY = process.env.CUELINKS_API_KEY
const CUELINKS_BASE_URL = process.env.CUELINKS_BASE_URL || 'https://www.cuelinks.com/api/v2'

export async function GET(req: NextRequest) {
  if (!CUELINKS_API_KEY) {
    return NextResponse.json({ error: 'Server misconfiguration: CUELINKS_API_KEY not set.' }, { status: 500 })
  }
  try {
    const { searchParams } = new URL(req.url)
    const search = (searchParams.get('search') || '').toLowerCase().trim()
    const category = (searchParams.get('category') || 'all').toLowerCase().trim()
    const payoutType = (searchParams.get('payoutType') || 'all').toLowerCase().trim()

    let allCampaigns: any[] = []

    // 1. Try local cached file first for super fast instant search
    const localFilePath = path.join(process.cwd(), 'all_cuelinks.json')
    if (fs.existsSync(localFilePath)) {
      try {
        const fileContent = fs.readFileSync(localFilePath, 'utf8')
        allCampaigns = JSON.parse(fileContent)
      } catch (e) {
        console.warn('Error reading local all_cuelinks.json:', e)
      }
    }

    // 2. If empty or requested fresh, fetch from CueLinks API
    if (allCampaigns.length === 0) {
      try {
        const clRes = await fetch(`${CUELINKS_BASE_URL}/campaigns.json?page=1&limit=50`, {
          headers: {
            Authorization: `Bearer ${CUELINKS_API_KEY}`,
            'Content-Type': 'application/json',
          },
          cache: 'no-store',
        })
        if (clRes.ok) {
          const json = await clRes.json()
          allCampaigns = json.campaigns || []
        }
      } catch (apiErr) {
        console.warn('CueLinks API fetch error:', apiErr)
      }
    }

    // 3. Format & Standardize
    const formatted = allCampaigns.map((c: any) => {
      const catName = c.categories?.[0]?.name || 'Shopping'
      const isLead = (c.payout_type || '').toLowerCase().includes('lead') || 
                     (c.conversion_flow && JSON.stringify(c.conversion_flow).toLowerCase().includes('lead')) ||
                     (c.conversion_flow && JSON.stringify(c.conversion_flow).toLowerCase().includes('signup'))
      
      const isFreeToTest = isLead || 
                           (c.name || '').toLowerCase().includes('card') || 
                           (c.name || '').toLowerCase().includes('loan') || 
                           (c.name || '').toLowerCase().includes('bank') || 
                           (c.name || '').toLowerCase().includes('demat') ||
                           (c.name || '').toLowerCase().includes('insurance') ||
                           (c.name || '').toLowerCase().includes('trial')

      return {
        id: c.id,
        name: c.name,
        domain: c.domain || (c.url ? new URL(c.url).hostname : 'merchant.com'),
        url: c.url,
        payout: c.payout,
        payout_type: c.payout_type || 'Per Sale(%)',
        payout_currency: c.payout_currency || 'INR',
        image: c.image || null,
        category: catName,
        reporting_type: c.reporting_type || 'Realtime',
        status: 'Approved & Live',
        cookie_duration: c.cookie_duration || '30 Days',
        is_free_to_test: isFreeToTest,
        test_type: isFreeToTest ? 'Free Lead / Signup (No Purchase Needed)' : 'Sale / Purchase Required',
        conversion_flow: c.conversion_flow || {
          'Step 1': 'User clicks the affiliate link',
          'Step 2': 'User visits merchant website/app',
          'Step 3': 'User completes action (Lead or Purchase)',
        },
        affiliate_url: c.affiliate_url || `https://linksredirect.com/?cid=301603&source=api&url=${encodeURIComponent(c.url || '')}`,
      }
    })

    // 4. Apply Filters
    let results = formatted

    if (search) {
      results = results.filter(
        (c) =>
          c.name.toLowerCase().includes(search) ||
          c.domain.toLowerCase().includes(search) ||
          c.category.toLowerCase().includes(search)
      )
    }

    if (category !== 'all') {
      results = results.filter((c) => c.category.toLowerCase().includes(category))
    }

    if (payoutType === 'free_lead') {
      results = results.filter((c) => c.is_free_to_test)
    } else if (payoutType === 'per_sale') {
      results = results.filter((c) => !c.is_free_to_test)
    }

    return NextResponse.json({
      total: results.length,
      totalLiveInCueLinks: formatted.length,
      campaigns: results,
    })
  } catch (err: any) {
    return NextResponse.json({ error: err.message || 'Internal Server Error' }, { status: 500 })
  }
}
