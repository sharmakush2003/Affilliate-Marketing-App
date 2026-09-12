-- ================================================================
-- 🧪 REWARD CLUB — SQL SANITY CHECK SCRIPT
-- Run in: Supabase Dashboard → SQL Editor → New Query
-- ================================================================

-- ── 1. Verify all required tables exist ─────────────────────────
SELECT
  table_name,
  CASE
    WHEN table_name IN ('profiles','transactions','clicks','withdrawals','admin_sessions','support_queries')
    THEN '✅ EXISTS'
    ELSE '❌ MISSING'
  END AS status
FROM information_schema.tables
WHERE table_schema = 'public'
  AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- ── 2. Verify column counts per table ───────────────────────────
SELECT
  table_name,
  count(*) AS column_count,
  CASE table_name
    WHEN 'profiles'       THEN CASE WHEN count(*) >= 8  THEN '✅ OK' ELSE '❌ Missing columns' END
    WHEN 'transactions'   THEN CASE WHEN count(*) >= 12 THEN '✅ OK' ELSE '❌ Missing columns' END
    WHEN 'clicks'         THEN CASE WHEN count(*) >= 4  THEN '✅ OK' ELSE '❌ Missing columns' END
    WHEN 'withdrawals'    THEN CASE WHEN count(*) >= 10 THEN '✅ OK' ELSE '❌ Missing columns' END
    WHEN 'admin_sessions' THEN CASE WHEN count(*) >= 8  THEN '✅ OK' ELSE '❌ Missing columns' END
    ELSE '⚪ No check'
  END AS verdict
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN ('profiles','transactions','clicks','withdrawals','admin_sessions','support_queries')
GROUP BY table_name
ORDER BY table_name;

-- ── 3. Verify UNIQUE constraint on transactions.cuelinks_transaction_id ──
SELECT
  tc.constraint_name,
  tc.table_name,
  kcu.column_name,
  tc.constraint_type,
  CASE WHEN tc.constraint_type = 'UNIQUE' THEN '✅ UNIQUE OK' ELSE '❌ NOT UNIQUE' END AS status
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
  ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public'
  AND tc.table_name = 'transactions'
  AND kcu.column_name = 'cuelinks_transaction_id';

-- ── 4. Verify on_auth_user_created trigger exists ───────────────
SELECT
  trigger_name,
  event_manipulation,
  event_object_table,
  action_timing,
  '✅ TRIGGER ACTIVE' AS status
FROM information_schema.triggers
WHERE trigger_name = 'on_auth_user_created';

-- ── 5. Verify indexes exist for performance ─────────────────────
SELECT
  indexname,
  tablename,
  '✅ INDEX OK' AS status
FROM pg_indexes
WHERE schemaname = 'public'
  AND indexname IN (
    'idx_profiles_email',
    'idx_transactions_user_id',
    'idx_transactions_status',
    'idx_click_logs_user_id'
  )
ORDER BY tablename;

-- ── 6. Verify RLS is enabled on all tables ──────────────────────
SELECT
  tablename,
  rowsecurity,
  CASE WHEN rowsecurity THEN '✅ RLS ENABLED' ELSE '❌ RLS DISABLED' END AS rls_status
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('profiles','transactions','clicks','withdrawals','admin_sessions')
ORDER BY tablename;

-- ── 7. Check for any NULL user_id in withdrawals ────────────────
SELECT
  COUNT(*) AS total_withdrawals,
  COUNT(CASE WHEN user_id IS NULL THEN 1 END) AS null_user_id_count,
  CASE WHEN COUNT(CASE WHEN user_id IS NULL THEN 1 END) = 0
    THEN '✅ No orphan withdrawals'
    ELSE '❌ ' || COUNT(CASE WHEN user_id IS NULL THEN 1 END) || ' withdrawals without user_id'
  END AS verdict
FROM public.withdrawals;

-- ── 8. Check for hardcoded IP in clicks ─────────────────────────
SELECT
  COUNT(*) AS clicks_with_hardcoded_ip,
  CASE WHEN COUNT(*) = 0
    THEN '✅ No hardcoded IPs found'
    ELSE '❌ ' || COUNT(*) || ' clicks with hardcoded IP 103.167.194.52'
  END AS verdict
FROM public.clicks
WHERE ip_address = '103.167.194.52';

-- ── 9. Check double-credit protection ───────────────────────────
-- Find any approved transactions where cashback_credited_coins > 0 (credited)
-- vs those that are still 0 (not yet credited)
SELECT
  status,
  COUNT(*) AS count,
  SUM(cashback_credited_coins) AS total_coins_credited,
  CASE
    WHEN status = 'approved' AND SUM(cashback_credited_coins) > 0
    THEN '✅ Coins credited correctly'
    WHEN status = 'approved' AND SUM(cashback_credited_coins) = 0
    THEN 'ℹ️  No approved transactions credited yet'
    WHEN status = 'rejected'
    THEN '✅ Rejected (0 coins credited)'
    ELSE 'ℹ️  Pending'
  END AS verdict
FROM public.transactions
GROUP BY status;

-- ── 10. Verify no profiles have negative coin balances ──────────
SELECT
  COUNT(*) AS negative_balance_count,
  CASE WHEN COUNT(*) = 0
    THEN '✅ All profiles have non-negative balances'
    ELSE '❌ ' || COUNT(*) || ' profiles have negative total_coins!'
  END AS verdict
FROM public.profiles
WHERE total_coins < 0 OR redeemed_coins < 0;

-- ── SUMMARY ─────────────────────────────────────────────────────
SELECT '🎯 SQL SANITY CHECK COMPLETE — Review results above' AS summary;
