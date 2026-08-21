import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function POST(req: NextRequest) {
  try {
    const { sessionId } = await req.json()
    if (!sessionId) {
      return NextResponse.json({ error: 'Session ID is required' }, { status: 400 })
    }

    const { error } = await supabaseAdmin
      .from('admin_sessions')
      .update({ status: 'logged_out' })
      .eq('id', sessionId)

    if (error) {
      console.error('[terminate-session] db update error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    return NextResponse.json({ success: true })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown terminate-session error'
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}
