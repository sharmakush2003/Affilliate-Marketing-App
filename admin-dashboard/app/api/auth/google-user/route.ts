import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function POST(req: NextRequest) {
  try {
    const { email, fullName, mobile } = await req.json()

    if (!email) {
      return NextResponse.json({ error: 'Email is required' }, { status: 400 })
    }

    const cleanEmail = email.trim().toLowerCase()
    const cleanName = fullName?.trim() || cleanEmail.split('@')[0]
    const cleanMobile = mobile?.trim() || ''

    // 1. Search for existing user in auth.users
    const { data: listData } = await supabaseAdmin.auth.admin.listUsers()
    const existingUser = listData?.users?.find(u => u.email?.toLowerCase() === cleanEmail)

    let userId = ''

    if (existingUser) {
      userId = existingUser.id
      // Auto-confirm user email & update metadata
      await supabaseAdmin.auth.admin.updateUserById(existingUser.id, {
        email_confirm: true,
        user_metadata: {
          full_name: cleanName,
          name: cleanName,
          mobile: cleanMobile,
          phone: cleanMobile,
        },
      })
    } else {
      // Create new user with email_confirm: true (INSTANT VERIFIED USER)
      const { data: createData, error: createErr } = await supabaseAdmin.auth.admin.createUser({
        email: cleanEmail,
        email_confirm: true, // ✅ AUTO-CONFIRMED: NO "Waiting for verification"
        user_metadata: {
          full_name: cleanName,
          name: cleanName,
          mobile: cleanMobile,
          phone: cleanMobile,
        },
      })

      if (createErr) {
        console.error('[google-user API] createUser error:', createErr.message)
        return NextResponse.json({ error: createErr.message }, { status: 500 })
      }
      userId = createData.user.id
    }

    // 2. Upsert public.profiles database table
    const { data: profileData, error: profileErr } = await supabaseAdmin
      .from('profiles')
      .upsert({
        id: userId,
        email: cleanEmail,
        full_name: cleanName,
        mobile: cleanMobile,
        total_coins: 0,
        redeemed_coins: 0,
        total_savings: 0,
      })
      .select()
      .single()

    if (profileErr) {
      console.error('[google-user API] profile upsert error:', profileErr.message)
    }

    return NextResponse.json({
      success: true,
      userId: userId,
      email: cleanEmail,
      fullName: cleanName,
      mobile: cleanMobile,
      profile: profileData,
    })
  } catch (err: any) {
    console.error('[google-user API] Exception:', err)
    return NextResponse.json({ error: err.message || 'Server error' }, { status: 500 })
  }
}
