import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function POST(req: NextRequest) {
  try {
    const { email, location, userAgent } = await req.json()
    
    // Detect IP address from headers
    const forwarded = req.headers.get('x-forwarded-for')
    const ipAddress = forwarded ? forwarded.split(',')[0] : '127.0.0.1'

    const { data, error } = await supabaseAdmin
      .from('admin_sessions')
      .insert({
        email,
        ip_address: ipAddress,
        location: location || 'Unknown Location',
        user_agent: userAgent || 'Unknown Browser',
        status: 'active',
      })
      .select('id')
      .single()

    if (error) {
      console.error('[log-session] db insert error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    return NextResponse.json({ sessionId: data.id })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown log-session error'
    console.error('[log-session] API error:', errorMsg)
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}
