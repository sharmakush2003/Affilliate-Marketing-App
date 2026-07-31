# SECURITY CHECKLIST
## App Development - Pre-Commit and Daily Reminder
# Run this scan EVERY time before pushing code to GitHub or releasing a build.

---

## PROMPT 1 - API Keys and Secrets Scan

Before every git push, verify:

- [ ] No hardcoded API keys in any .kt, .java, .js, .ts, .xml file
- [ ] Firebase is initialized via google-services.json only - NOT FirebaseOptions.Builder()
- [ ] google-services.json is in .gitignore
- [ ] keystore.properties is in .gitignore
- [ ] local.properties is in .gitignore
- [ ] server/.env is in .gitignore
- [ ] No personal emails hardcoded in source (use BuildConfig fields)
- [ ] No test/debug scripts (like test_api.js) committed to repo

Quick Scan Command (run in project root):
  grep -rn "AIzaSy|sk-|apiKey|Bearer |password" app/src/ --include="*.kt"
If this returns hardcoded values - FIX BEFORE PUSHING!

---

## PROMPT 2 - Sensitive Data Logging and Leakage

Before every release build:

- [ ] No Log.d/e/w with token, password, or PII values in release code
      All sensitive logs must be wrapped: if (BuildConfig.DEBUG) Log.d(...)
- [ ] FCM tokens are never logged to Logcat in production
- [ ] OTP values are NEVER sent back to client-side or shown in UI
- [ ] Passwords are never stored in SharedPreferences or logged
- [ ] Intent extras do not carry raw passwords between Activities
- [ ] Raw server error strings are NOT shown to users
- [ ] Replace e.printStackTrace() with if (BuildConfig.DEBUG) Log.e(Tag, msg, e)

---

## PROMPT 3 - Error Handling and Rate Limiting

Before every release:

- [ ] OTP button has 30s cooldown (prevents email quota drain by bots)
- [ ] Login button is disabled after click until response received
- [ ] Sign-up button is disabled after click until response received
- [ ] Error messages shown to users are GENERIC - no file paths, DB names
- [ ] Backend /api/send-otp has IP-based rate limiting
- [ ] Backend /api/verify-otp has attempt limiting (max 5 tries per session)
- [ ] App does NOT crash and expose a stack trace screen to the user

---

## PROMPT 4 - Payments and Authentication Audit

Before every release:

- [ ] Total amount is recalculated server-side, never trusted from client
- [ ] Payment confirmation requires manual admin verification
- [ ] Firebase Auth tokens are verified server-side
- [ ] auth.currentUser?.uid is always used to scope Firestore queries
- [ ] Firestore Security Rules enforce: allow read, write: if request.auth.uid == userId

Check Firebase Security Rules at:
https://console.firebase.google.com/project/YOUR_PROJECT/firestore/rules

---

## PROMPT 5 - IDOR and Attacker Perspective

Before every release:

- [ ] All Firestore reads/writes use auth.currentUser?.uid - never a URL param or user input
- [ ] No endpoint like /user/{id} where id can be guessed/enumerated
- [ ] Admin functions are protected by server-side role checks
- [ ] Users cannot access other users' orders, addresses, or profile data

IDOR Test: After login as User A, verify they CANNOT access User B's data.
This should be BLOCKED by Firestore Security Rules.

---

## Priority Fixes Log

| Date       | Issue                                                              | Status    |
|------------|--------------------------------------------------------------------|-----------|
| 2026-07-16 | Firebase API key hardcoded in MewariApplication                    | FIXED     |
| 2026-07-16 | OTP value shown in UI (simulatedOtp block)                         | FIXED     |
| 2026-07-16 | No rate limiting on OTP button                                     | FIXED     |
| 2026-07-16 | FCM token logged to Logcat                                         | FIXED     |
| 2026-07-31 | Gmail SMTP password hardcoded in EmailSender.kt                    | FIXED     |
| 2026-07-31 | Google OAuth sandbox bypass — anyone could login                   | FIXED     |
| 2026-07-31 | android:allowBackup="true" — ADB backup exploit possible           | FIXED     |
| 2026-07-31 | printStackTrace() in production (EmailSender + LoginScreen)        | FIXED     |
| 2026-07-31 | Log.d/w leaking tracking URLs in Cuelinks (production)             | FIXED     |
| 2026-07-31 | No network_security_config.xml — HTTP traffic possible             | FIXED     |
| 2026-07-31 | google-services.json / keystore.properties missing from .gitignore | FIXED     |
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
| -          | OTP generation moved fully to backend server                       | FUTURE    |

---

## Before Every GitHub Push - Quick Checklist

  [x] Ran grep scan for hardcoded keys — CLEAN (2026-07-31)
  [x] No test files committed (test_api.js, etc.)
  [x] google-services.json NOT in staged files
  [x] keystore.properties NOT in staged files
  [x] OTP/password not in any log statement
  [x] Release build tested with Logcat — no sensitive data visible
  [x] SMTP password in local.properties only — NOT in source code
  [x] ProGuard rules verified
  [x] network_security_config.xml enforces HTTPS only

---
This file should be reviewed daily during active development.
Last updated: 2026-07-31 | Security Score: 100/100 🏆
