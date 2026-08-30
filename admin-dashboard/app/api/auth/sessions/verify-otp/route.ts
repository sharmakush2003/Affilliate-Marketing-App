import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function POST(req: NextRequest) {
  try {
    const { sessionId, otpCode } = await req.json()

    if (!sessionId || !otpCode) {
      return NextResponse.json({ error: 'Session ID and OTP code are required' }, { status: 400 })
    }

    // Fetch session from DB
    const { data: session, error: fetchErr } = await supabaseAdmin
      .from('admin_sessions')
      .select('status, otp_code, otp_expires')
      .eq('id', sessionId)
      .single()

    if (fetchErr || !session) {
      return NextResponse.json({ error: 'Session not found. Please login again.' }, { status: 404 })
    }

    if (session.status !== 'pending_2fa') {
      return NextResponse.json({ error: 'Session is not awaiting 2FA verification.' }, { status: 400 })
    }

    // Check expiry
    if (!session.otp_expires || new Date() > new Date(session.otp_expires)) {
      // Mark session as logged_out since OTP expired
      await supabaseAdmin
        .from('admin_sessions')
        .update({ status: 'logged_out' })
        .eq('id', sessionId)
      return NextResponse.json({ error: 'Verification code has expired. Please login again.' }, { status: 401 })
    }

    // Validate OTP
    if (session.otp_code !== otpCode.trim()) {
      return NextResponse.json({ error: 'Invalid verification code. Please try again.' }, { status: 401 })
    }

    // Activate session
    const { error: updateErr } = await supabaseAdmin
      .from('admin_sessions')
      .update({
        status: 'active',
        otp_code: null,
        otp_expires: null,
        last_active: new Date().toISOString(),
      })
      .eq('id', sessionId)

    if (updateErr) {
      console.error('[verify-otp] db update error:', updateErr.message)
      return NextResponse.json({ error: updateErr.message }, { status: 500 })
    }

    return NextResponse.json({ success: true })
  } catch (err: unknown) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown verify-otp error'
    console.error('[verify-otp] API error:', errorMsg)
    return NextResponse.json({ error: errorMsg }, { status: 500 })
  }
}
