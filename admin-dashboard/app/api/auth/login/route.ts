import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'
import { isAllowedAdminEmail } from '@/lib/adminAuth'

export async function POST(req: NextRequest) {
  try {
    const { email, password } = await req.json()
    if (!email || !password) {
      return NextResponse.json({ error: 'Email and password are required' }, { status: 400 })
    }

    const cleanEmail = email.trim().toLowerCase()

    // 1. Enforce admin allowlist verification
    if (!isAllowedAdminEmail(cleanEmail)) {
      console.warn(`[auth/login] Blocked unauthorized login attempt: ${cleanEmail}`)
      return NextResponse.json(
        { error: 'Access Denied: You are not an authorized admin.' },
        { status: 403 }
      )
    }

    // 2. Check if user exists in Supabase Auth using Admin API
    const { data: usersData, error: listError } = await supabaseAdmin.auth.admin.listUsers()
    
    if (listError) {
      console.error('[auth/login] listUsers error:', listError.message)
      return NextResponse.json({ error: 'Internal auth verification error' }, { status: 500 })
    }

    const targetUser = usersData?.users?.find(
      (u) => u.email?.trim().toLowerCase() === cleanEmail
    )

    if (!targetUser) {
      return NextResponse.json(
        { error: 'Invalid admin credentials or account does not exist.' },
        { status: 401 }
      )
    }

    // 3. Return success so client can proceed with secure supabase.auth.signInWithPassword
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
