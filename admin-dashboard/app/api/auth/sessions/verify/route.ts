import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function GET(req: NextRequest) {
  try {
    const id = req.nextUrl.searchParams.get('id')
    if (!id) {
      return NextResponse.json({ valid: false })
    }

    const { data, error } = await supabaseAdmin
      .from('admin_sessions')
      .select('status')
      .eq('id', id)
      .single()

    if (error || !data) {
      return NextResponse.json({ valid: false })
    }

    if (data.status === 'active') {
      // Update last active timestamp
      await supabaseAdmin
        .from('admin_sessions')
        .update({ last_active: new Date().toISOString() })
        .eq('id', id)
    }

    return NextResponse.json({ valid: data.status === 'active' })
  } catch {
    return NextResponse.json({ valid: false })
  }
}
