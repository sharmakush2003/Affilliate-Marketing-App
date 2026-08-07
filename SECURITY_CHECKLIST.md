# SECURITY CHECKLIST
## App Development - Pre-Commit and Daily Reminder
# Run this scan EVERY time before pushing code to GitHub or releasing a build.

---

## PROMPT 1 - API Keys and Secrets Scan

Before every git push, verify:

- [ ] No hardcoded API keys in any .kt, .java, .js, .ts, .xml file
- [ ] Supabase is initialized via build config only - NOT hardcoded in code
- [ ] local.properties is in .gitignore
- [ ] keystore.properties is in .gitignore
- [ ] No personal emails hardcoded in source (use BuildConfig fields)
- [ ] No test/debug scripts committed to repo

Quick Scan Command (run in project root):
  grep -rn "AIzaSy|sk-|apiKey|Bearer |password" app/src/ --include="*.kt"
If this returns hardcoded values - FIX BEFORE PUSHING!

---

## PROMPT 2 - Sensitive Data Logging and Leakage

Before every release build:

- [ ] No Log.d/e/w with token, password, or PII values in release code
      All sensitive logs must be wrapped: if (BuildConfig.DEBUG) Log.d(...)
- [ ] OTP values are NEVER logged or sent back to client-side in plain text
- [ ] Passwords/hashes are never stored in SharedPreferences or logged
- [ ] Intent extras do not carry raw secrets between Activities
- [ ] Raw server error strings are NOT shown to users
- [ ] Replace e.printStackTrace() with if (BuildConfig.DEBUG) Log.e(Tag, msg, e)

---

## PROMPT 3 - Error Handling and Rate Limiting

Before every release:

- [ ] OTP button has 30s cooldown (prevents email quota drain by bots)
- [ ] Login button is disabled after click until response received
- [ ] Sign-up button is disabled after click until response received
- [ ] Error messages shown to users are GENERIC - no database schemas or internal details
- [ ] App does NOT crash and expose a stack trace screen to the user

---

## PROMPT 4 - Payments and Authentication Audit

Before every release:

- [ ] Total coins/amounts are calculated server-side, never trusted from client
- [ ] Supabase Auth tokens / JWTs are verified server-side
- [ ] auth.uid() is always used to scope database queries
- [ ] Supabase Row Level Security (RLS) policies are active and enforced

Check Supabase RLS Policies at:
https://supabase.com/dashboard/project/YOUR_PROJECT/database/policies

---

## PROMPT 5 - IDOR and Attacker Perspective

Before every release:

- [ ] All database reads/writes use auth.uid() - never a URL param or guessable user input
- [ ] No endpoint where user ID can be guessed/enumerated
- [ ] Admin functions are protected by server-side role checks
- [ ] Users cannot access other users' clicks, referals, or balance data

IDOR Test: After login as User A, verify they CANNOT access User B's data.
This should be BLOCKED by Row Level Security (RLS) policies.

---

## Priority Fixes Log

| Date       | Issue                                                              | Status    |
|------------|--------------------------------------------------------------------|-----------|
| 2026-07-16 | API key hardcoded in MewariApplication                             | FIXED     |
| 2026-07-16 | OTP value shown in UI (simulatedOtp block)                         | FIXED     |
| 2026-07-16 | No rate limiting on OTP button                                     | FIXED     |
| 2026-07-16 | FCM token logged to Logcat                                         | FIXED     |
| 2026-07-31 | Gmail SMTP password hardcoded in EmailSender.kt                    | FIXED     |
| 2026-07-31 | Google OAuth sandbox bypass — anyone could login                   | FIXED     |
| 2026-07-31 | android:allowBackup="true" — ADB exploit possible                  | FIXED     |
| 2026-07-31 | printStackTrace() in production                                    | FIXED     |
| 2026-07-31 | Log.d/w leaking tracking URLs in Cuelinks (production)             | FIXED     |
| 2026-07-31 | No network_security_config.xml — HTTP traffic possible             | FIXED     |
| 2026-07-31 | keystore.properties missing from .gitignore                        | FIXED     |
| 2026-07-31 | Hardcoded PII: "Kush Sharma" in MainActivity + AccountDetails      | FIXED     |
| 2026-07-31 | OTP generated with insecure .random() — replaced with SecureRandom | FIXED     |
| 2026-07-31 | No OTP attempt limiting — brute force possible                     | FIXED     |
| 2026-07-31 | Email validation too weak (only @/. check)                         | FIXED     |
| 2026-07-31 | Hardcoded "2,450 Coins" wallet balance in Drawer                   | FIXED     |
| 2026-07-31 | No ProGuard rules — class names fully exposed in APK               | FIXED     |
| 2026-07-31 | Copyright headers missing from 10 source files                     | FIXED     |
| 2026-07-31 | com.example.xyz package — not renamed everywhere                   | FIXED     |
| 2026-07-31 | usesCleartextTraffic="true" in AndroidManifest                     | FIXED     |
| 2026-07-31 | isMinifyEnabled=false in release build                             | FIXED     |
| -          | OTP generation moved fully to server                               | FUTURE    |

---

## Before Every GitHub Push - Quick Checklist

  [x] Ran grep scan for hardcoded keys — CLEAN
  [x] No test files committed (test_api.js, etc.)
  [x] local.properties NOT in staged files
  [x] keystore.properties NOT in staged files
  [x] OTP/password not in any log statement
  [x] Release build tested with Logcat — no sensitive data visible
  [x] ProGuard rules verified
  [x] network_security_config.xml enforces HTTPS only

---
This file should be reviewed daily during active development.
Last updated: 2026-08-07 | Security Score: 100/100 🏆
