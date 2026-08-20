-- ════════════════════════════════════════════════════════════════
-- Reward Club Admin Dashboard - Supabase Schema
-- Run this in your Supabase SQL Editor (dashboard.supabase.com)
-- ════════════════════════════════════════════════════════════════

-- Create transactions table (run once)
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    cuelinks_transaction_id TEXT UNIQUE NOT NULL,
    campaign_name TEXT NOT NULL,
    merchant_name TEXT,
    order_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    commission_earned NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    cashback_credited_coins BIGINT NOT NULL DEFAULT 0,
    status TEXT NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'approved', 'rejected')),
    payment_status TEXT NOT NULL DEFAULT 'unpaid' CHECK (payment_status IN ('unpaid', 'paid')),
    click_time TIMESTAMP WITH TIME ZONE,
    transaction_time TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Index for fast user transactions lookup
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON public.transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON public.transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_payment_status ON public.transactions(payment_status);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON public.transactions(created_at DESC);

-- Enable Row Level Security (allow service role unrestricted access)
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

-- Policy: Allow service role to do everything (used by admin dashboard API routes)
CREATE POLICY "Service role full access" ON public.transactions
  FOR ALL USING (true) WITH CHECK (true);

-- Confirm profiles table has created_at column
ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now());
