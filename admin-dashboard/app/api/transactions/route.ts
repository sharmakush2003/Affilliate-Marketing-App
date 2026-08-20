import { NextRequest, NextResponse } from 'next/server'
import { supabaseAdmin } from '@/lib/supabase'

// GET: Paginated transactions with user profile join
export async function GET(req: NextRequest) {
  const { searchParams } = req.nextUrl
  const page = parseInt(searchParams.get('page') ?? '1')
  const limit = parseInt(searchParams.get('limit') ?? '20')
  const status = searchParams.get('status') ?? ''
  const paymentStatus = searchParams.get('payment_status') ?? ''
  const search = searchParams.get('search') ?? ''
  const from = (page - 1) * limit
  const to = from + limit - 1

  let query = supabaseAdmin
    .from('transactions')
    .select('*, profiles(id, full_name, email)', { count: 'exact' })
    .order('created_at', { ascending: false })
    .range(from, to)

  if (status) query = query.eq('status', status)
  if (paymentStatus) query = query.eq('payment_status', paymentStatus)
  if (search) {
    query = query.or(`campaign_name.ilike.%${search}%,merchant_name.ilike.%${search}%`)
  }

  const { data, error, count } = await query
  if (error) return NextResponse.json({ error: error.message }, { status: 500 })
  return NextResponse.json({ transactions: data, total: count, page, limit })
}

// PATCH: Update transaction payment status and optionally re-credit coins
export async function PATCH(req: NextRequest) {
  const { transactionId, paymentStatus } = await req.json()

  if (!transactionId || !paymentStatus) {
    return NextResponse.json({ error: 'transactionId and paymentStatus required' }, { status: 400 })
  }

  const { data: tx, error: fetchErr } = await supabaseAdmin
    .from('transactions')
    .select('*')
    .eq('id', transactionId)
    .single()

  if (fetchErr || !tx) return NextResponse.json({ error: 'Transaction not found' }, { status: 404 })

  const { data, error } = await supabaseAdmin
    .from('transactions')
    .update({ payment_status: paymentStatus })
    .eq('id', transactionId)
    .select()
    .single()

  if (error) return NextResponse.json({ error: error.message }, { status: 500 })
  return NextResponse.json({ success: true, transaction: data })
}
