-- ════════════════════════════════════════════════════════════════
-- Reward Club Admin Sessions Logging Schema
-- Run this in your Supabase SQL Editor (dashboard.supabase.com)
-- ════════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS public.admin_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT NOT NULL,
    ip_address TEXT,
    location TEXT,
    user_agent TEXT,
    status TEXT NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'logged_out', 'pending_2fa')),
    otp_code TEXT,
    otp_expires TIMESTAMP WITH TIME ZONE,
    last_active TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Index for fast lookup
CREATE INDEX IF NOT EXISTS idx_admin_sessions_email ON public.admin_sessions(email);
CREATE INDEX IF NOT EXISTS idx_admin_sessions_status ON public.admin_sessions(status);
CREATE INDEX IF NOT EXISTS idx_admin_sessions_created_at ON public.admin_sessions(created_at DESC);

-- Enable RLS
ALTER TABLE public.admin_sessions ENABLE ROW LEVEL SECURITY;

-- Policy: Allow service role full access
DROP POLICY IF EXISTS "Service role full access on admin_sessions" ON public.admin_sessions;
CREATE POLICY "Service role full access on admin_sessions" ON public.admin_sessions
  FOR ALL USING (true) WITH CHECK (true);

-- ── Run this if table already exists (migration) ──
-- ALTER TABLE public.admin_sessions ADD COLUMN IF NOT EXISTS otp_code TEXT;
-- ALTER TABLE public.admin_sessions ADD COLUMN IF NOT EXISTS otp_expires TIMESTAMP WITH TIME ZONE;
-- ALTER TABLE public.admin_sessions DROP CONSTRAINT IF EXISTS admin_sessions_status_check;
-- ALTER TABLE public.admin_sessions ADD CONSTRAINT admin_sessions_status_check CHECK (status IN ('active', 'logged_out', 'pending_2fa'));
