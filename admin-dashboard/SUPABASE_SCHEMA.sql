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

-- Create click_logs table for Cuelinks / Mobile App click tracking
CREATE TABLE IF NOT EXISTS public.click_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    sub_id TEXT,
    campaign_name TEXT NOT NULL,
    channel_id TEXT DEFAULT '301603',
    source TEXT DEFAULT 'api',
    platform TEXT DEFAULT 'mobile',
    ip_address TEXT,
    destination_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Indexes for click_logs
CREATE INDEX IF NOT EXISTS idx_click_logs_user_id ON public.click_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_click_logs_campaign ON public.click_logs(campaign_name);
CREATE INDEX IF NOT EXISTS idx_click_logs_created_at ON public.click_logs(created_at DESC);

ALTER TABLE public.click_logs ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Service role click_logs access" ON public.click_logs
  FOR ALL USING (true) WITH CHECK (true);

-- Create offers table for CueLinks Offers & Coupon Deals Sync
CREATE TABLE IF NOT EXISTS public.offers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cuelinks_offer_id BIGINT UNIQUE,
    campaign_name TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    coupon_code TEXT,
    category TEXT,
    merchant_name TEXT,
    image_url TEXT,
    target_url TEXT,
    affiliate_url TEXT,
    valid_till TIMESTAMP WITH TIME ZONE,
    status TEXT DEFAULT 'live',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_offers_campaign ON public.offers(campaign_name);
CREATE INDEX IF NOT EXISTS idx_offers_category ON public.offers(category);
CREATE INDEX IF NOT EXISTS idx_offers_valid_till ON public.offers(valid_till DESC);

ALTER TABLE public.offers ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Service role offers access" ON public.offers
  FOR ALL USING (true) WITH CHECK (true);


