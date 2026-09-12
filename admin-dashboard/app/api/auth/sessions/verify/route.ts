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
      .select('status, otp_expires, created_at')
      .eq('id', id)
      .single()

    if (error || !data) {
      return NextResponse.json({ valid: false })
    }

    // ✅ FIX: Check BOTH status AND OTP expiry
    // A session is only valid if:
    //   1. status === 'active' (admin completed 2FA)
    //   2. Either no otp_expires set, OR the OTP window hasn't expired
    const now = new Date()
    const otpExpiry = data.otp_expires ? new Date(data.otp_expires) : null
    const isOtpValid = !otpExpiry || otpExpiry > now

    const isValid = data.status === 'active' && isOtpValid

    if (isValid) {
      // Update last active timestamp
      await supabaseAdmin
        .from('admin_sessions')
        .update({ last_active: new Date().toISOString() })
        .eq('id', id)
    }

    return NextResponse.json({ valid: isValid })
  } catch {
    return NextResponse.json({ valid: false })
  }
}
