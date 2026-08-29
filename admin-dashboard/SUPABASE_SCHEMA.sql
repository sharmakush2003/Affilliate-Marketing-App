-- Create profiles table (if not exists)
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    email TEXT,
    full_name TEXT,
    mobile TEXT,
    total_coins BIGINT DEFAULT 0,
    redeemed_coins BIGINT DEFAULT 0,
    total_savings NUMERIC(10, 2) DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Index for profiles
CREATE INDEX IF NOT EXISTS idx_profiles_email ON public.profiles(email);
CREATE INDEX IF NOT EXISTS idx_profiles_created_at ON public.profiles(created_at DESC);

ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Service role profiles access" ON public.profiles
  FOR ALL USING (true) WITH CHECK (true);

-- Automatic trigger: create/update profile ONLY when user verifies email / OTP
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
BEGIN
  -- Only create profile row if the user's email/OTP has been confirmed
  IF NEW.email_confirmed_at IS NOT NULL THEN
    INSERT INTO public.profiles (id, email, full_name, mobile, total_coins, redeemed_coins, total_savings, created_at)
    VALUES (
      NEW.id,
      NEW.email,
      COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.raw_user_meta_data->>'display_name', NEW.raw_user_meta_data->>'name', ''),
      COALESCE(NEW.raw_user_meta_data->>'mobile', NEW.raw_user_meta_data->>'phone', ''),
      0,
      0,
      0,
      NEW.created_at
    )
    ON CONFLICT (id) DO UPDATE SET
      email = EXCLUDED.email,
      full_name = CASE WHEN EXCLUDED.full_name <> '' THEN EXCLUDED.full_name ELSE public.profiles.full_name END,
      mobile = CASE WHEN EXCLUDED.mobile <> '' THEN EXCLUDED.mobile ELSE public.profiles.mobile END;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

-- Trigger execution on auth.users insert
DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();

-- Trigger execution when email_confirmed_at is updated on auth.users (after OTP verification)
CREATE OR REPLACE FUNCTION public.handle_user_confirmed()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.email_confirmed_at IS NULL AND NEW.email_confirmed_at IS NOT NULL THEN
    INSERT INTO public.profiles (id, email, full_name, mobile, total_coins, redeemed_coins, total_savings, created_at)
    VALUES (
      NEW.id,
      NEW.email,
      COALESCE(NEW.raw_user_meta_data->>'full_name', NEW.raw_user_meta_data->>'display_name', NEW.raw_user_meta_data->>'name', ''),
      COALESCE(NEW.raw_user_meta_data->>'mobile', NEW.raw_user_meta_data->>'phone', ''),
      0,
      0,
      0,
      NEW.created_at
    )
    ON CONFLICT (id) DO UPDATE SET
      email = EXCLUDED.email,
      full_name = CASE WHEN EXCLUDED.full_name <> '' THEN EXCLUDED.full_name ELSE public.profiles.full_name END,
      mobile = CASE WHEN EXCLUDED.mobile <> '' THEN EXCLUDED.mobile ELSE public.profiles.mobile END;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_confirmed ON auth.users;
CREATE TRIGGER on_auth_user_confirmed
  AFTER UPDATE ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_user_confirmed();

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



