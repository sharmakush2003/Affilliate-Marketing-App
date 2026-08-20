import { createClient } from '@supabase/supabase-js'

const supabaseUrl = process.env.NEXT_PUBLIC_SUPABASE_URL!
const supabaseAnonKey = process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!

export const supabase = createClient(supabaseUrl, supabaseAnonKey)

// Service role client for server-side admin operations (mutations)
export const supabaseAdmin = createClient(
  supabaseUrl,
  process.env.SUPABASE_SERVICE_ROLE_KEY || supabaseAnonKey,
  {
    auth: {
      autoRefreshToken: false,
      persistSession: false,
    },
  }
)

// TypeScript types matching your Supabase schema
export type Profile = {
  id: string
  email: string
  full_name: string | null
  mobile: string | null
  total_coins: number | null
  redeemed_coins: number | null
  total_savings: number | null
  created_at?: string
}

export type Transaction = {
  id: string
  user_id: string | null
  cuelinks_transaction_id: string
  campaign_name: string
  merchant_name: string | null
  order_amount: number
  commission_earned: number
  cashback_credited_coins: number
  status: 'pending' | 'approved' | 'rejected'
  payment_status: 'unpaid' | 'paid'
  click_time: string | null
  transaction_time: string | null
  created_at: string
  profiles?: Profile
}
