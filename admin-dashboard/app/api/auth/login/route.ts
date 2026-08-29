import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

export async function POST(req: NextRequest) {
  try {
    const { email, password } = await req.json()
    if (!email || !password) {
      return NextResponse.json({ error: 'Email and password are required' }, { status: 400 })
    }

    const cleanEmail = email.trim().toLowerCase()

    // 1. Check if user exists in Supabase Auth using Admin API
    const { data: usersData, error: listError } = await supabaseAdmin.auth.admin.listUsers()
    
    let targetUser = usersData?.users?.find(
      (u) => u.email?.trim().toLowerCase() === cleanEmail
    )

    if (!targetUser) {
      // Create admin user in Supabase Auth if not present
      const { data: newUserData, error: createError } = await supabaseAdmin.auth.admin.createUser({
        email: cleanEmail,
        password: password,
        email_confirm: true,
      })

      if (createError) {
        return NextResponse.json({ error: createError.message }, { status: 400 })
      }
      targetUser = newUserData.user
    } else {
      // Ensure user password is updated & email confirmed for admin access
      await supabaseAdmin.auth.admin.updateUserById(targetUser.id, {
        password: password,
        email_confirm: true,
      })
    }

    // 2. Generate Session Token or return success
    return NextResponse.json({
      success: true,
      userId: targetUser.id,
      email: cleanEmail,
    })
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : 'Login failed'
    return NextResponse.json({ error: msg }, { status: 500 })
  }
}
