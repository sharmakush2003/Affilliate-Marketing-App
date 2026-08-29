import { NextRequest, NextResponse } from 'next/server'
import { createClient } from '@supabase/supabase-js'

export async function GET(req: NextRequest) {
  const { searchParams } = new URL(req.url)
  const token_hash = searchParams.get('token_hash')
  const type = searchParams.get('type')

  if (token_hash && type) {
    // Use anon client to verify the OTP
    const supabase = createClient(
      process.env.NEXT_PUBLIC_SUPABASE_URL!,
      process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
    )

    const { data, error } = await supabase.auth.verifyOtp({
      token_hash,
      type: type as any,
    })

    if (!error && data?.user && data?.session) {
      const user = data.user
      const { access_token, refresh_token } = data.session

      // Use service role to upsert the profile (bypass RLS)
      const supabaseAdmin = createClient(
        process.env.NEXT_PUBLIC_SUPABASE_URL!,
        process.env.SUPABASE_SERVICE_ROLE_KEY!
      )

      // Upsert profile into public.profiles table
      const fullName = user.user_metadata?.full_name || user.user_metadata?.name || ''
      const mobile = user.user_metadata?.mobile || user.user_metadata?.phone || ''

      await supabaseAdmin.from('profiles').upsert({
        id: user.id,
        email: user.email ?? '',
        full_name: fullName,
        mobile: mobile,
        total_coins: 0,
        redeemed_coins: 0,
        total_savings: 0,
      }, { onConflict: 'id', ignoreDuplicates: false })

      // Redirect to verify-email page with tokens so app can auto-login
      const redirectUrl = new URL('/verify-email', req.url)
      redirectUrl.searchParams.set('access_token', access_token)
      redirectUrl.searchParams.set('refresh_token', refresh_token)
      redirectUrl.searchParams.set('email', user.email ?? '')
      return NextResponse.redirect(redirectUrl)
    }
  }

  // On error, redirect to verify-email with error param
  return NextResponse.redirect(new URL('/verify-email?error=1', req.url))
}
