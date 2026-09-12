import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function GET(req: NextRequest) {
  try {
    const { data, error } = await supabaseAdmin
      .from('admin_sessions')
      .select('*')
      .order('created_at', { ascending: false })
      .limit(10)

    if (error) {
      console.error('[sessions] db fetch error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    return NextResponse.json({ sessions: data })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown sessions fetch error'
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}

export async function DELETE(req: NextRequest) {
  try {
    const { searchParams } = req.nextUrl
    const sessionId = searchParams.get('id')
    
    if (!sessionId) {
      return NextResponse.json({ error: 'Session ID is required' }, { status: 400 })
    }

    const { error } = await supabaseAdmin
      .from('admin_sessions')
      .delete()
      .eq('id', sessionId)

    if (error) {
      console.error('[sessions] db delete error:', error.message)
      return NextResponse.json({ error: error.message }, { status: 500 })
    }

    return NextResponse.json({ success: true })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown sessions delete error'
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}
