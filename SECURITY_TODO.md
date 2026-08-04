# SECURITY FIXES — RESUME KARO YAHAN SE!
## Created: 2026-08-04 | Audit Score: 62/100 → Target: 95/100
##
## YEH FILE DEKHI? Matlab kuch IMPORTANT kaam baaki hai!
## Neeche STEP by STEP guide hai — bas follow karo.

---

## ALREADY DONE (aaj 2026-08-04 ko complete hua)

- [x] F-06: printStackTrace() fix in Cuelinks.kt
- [x] F-05: Google Sign-In null-account bypass removed (LoginScreen.kt)
- [x] F-11: Hardcoded version v3.2.1 -> BuildConfig.VERSION_NAME (MainActivity.kt)
- [x] F-09: ProGuard bad -keep rule on EmailSender removed (proguard-rules.pro)
- [x] F-08: .gitignore wildcard for KEYSTORE_INFO.md fixed
- [x] F-01 setup: otp-server/ folder created (server.js, package.json, .env)
- [x] F-01 setup: SMTP_EMAIL + SMTP_PASSWORD removed from local.properties & build.gradle.kts
- [x] F-01 setup: OtpApiClient.kt created (app/src/main/java/.../utils/OtpApiClient.kt)

---

## BAAKI HAI — YEH KARNA HAI (IMPORTANT!)

### STEP 1 — OTP Server Deploy karo on Render.com (FREE, no credit card)
Bina is step ke OTP feature kaam nahi karega!

1. Jao: https://render.com => Sign Up (GitHub se)
2. New => Web Service => Connect your GitHub repo
3. Root Directory: otp-server
4. Build Command: npm install
5. Start Command: node server.js
6. Environment Variables mein ye 3 add karo:
   SMTP_EMAIL     = rewardclub.team@gmail.com
   SMTP_PASSWORD  = trwqwwxttewvvuoi
   API_SECRET     = 956c7c4d3225b8a4235ee953c58ab6818fae14aade057d6e2bea1a1521e7cd9c
7. Deploy ke baad URL milega (e.g. https://reward-club-otp.onrender.com)
8. Woh URL local.properties mein update karo:
   OTP_SERVER_URL=https://reward-club-otp.onrender.com

---

### STEP 2 — LoginScreen.kt update karo

LoginScreen.kt mein EmailSender ko OtpApiClient se replace karna hai.
File: app/src/main/java/com/rewardclub/app/ui/screens/LoginScreen.kt

A) Import badlo (top mein):
   HATAO:  import com.rewardclub.app.utils.EmailSender
   LAGAO:  import com.rewardclub.app.utils.OtpApiClient

B) generatedOtp variable HATAO (line ~52):
   HATAO:  var generatedOtp by remember { mutableStateOf("") }
   (OTP ab server pe stored hoga - Android ko value nahi chahiye)

C) "GET OTP" button click mein (line ~303) badlo:
   HATAO yeh sab:
      val secureRandom = java.security.SecureRandom()
      val code = (1000 + secureRandom.nextInt(9000)).toString()
      generatedOtp = code
      scope.launch(Dispatchers.IO) {
          val success = EmailSender.sendOtpEmail(emailAddress, code)
          ...
      }
   LAGAO:
      isSendingEmail = true
      scope.launch(Dispatchers.IO) {
          val result = OtpApiClient.sendOtp(emailAddress.trim())
          scope.launch(Dispatchers.Main) {
              isSendingEmail = false
              if (result.success) {
                  isOtpSent = true
                  Toast.makeText(context, "OTP Sent!", Toast.LENGTH_LONG).show()
              } else {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
              }
          }
      }

D) "VERIFY & PROCEED" button click mein (line ~416) badlo:
   HATAO:
      if (otpCode == generatedOtp) { onLoginSuccess() }
      else { otpAttempts++... }
   LAGAO:
      scope.launch(Dispatchers.IO) {
          val result = OtpApiClient.verifyOtp(emailAddress.trim(), otpCode)
          scope.launch(Dispatchers.Main) {
              if (result.verified) {
                  Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                  onLoginSuccess()
              } else {
                  Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                  if (result.remaining == 0) { otpCode = ""; isOtpSent = false }
              }
          }
      }

---

### STEP 3 — EmailSender.kt DELETE karo

File to delete: app/src/main/java/com/rewardclub/app/utils/EmailSender.kt
OtpApiClient.kt ne isko replace kar diya — ab yeh file useless hai.

Also remove these 2 lines from app/build.gradle.kts dependencies:
  implementation("com.sun.mail:android-mail:1.6.7")
  implementation("com.sun.mail:android-activation:1.6.7")

---

### STEP 4 — functions/ folder DELETE karo

Path: Affiliate-Marketing-App/functions/
Galti se Firebase Functions folder ban gaya tha — Spark plan pe kaam nahi karta.
Render.com wala otp-server/ hi use karna hai. functions/ folder delete karo.

---

### STEP 5 — KEYSTORE_INFO.md DELETE karo (disk se)

Path: Affiliate-Marketing-App/KEYSTORE_INFO.md
Keystore password (RewardClub@2026) plaintext mein hai — dangerous on disk!
Password already yaad hai ya password manager mein rakho, file hatao.

---

### STEP 6 — Google Sign-In Web Client ID lagao (LoginScreen.kt line ~67)

Firebase Console => Authentication => Sign-in method => Google
=> Web Client ID copy karo (looks like: 123456-abc.apps.googleusercontent.com)

LoginScreen.kt mein badlo:
  FROM: .requestIdToken("com.rewardclub.app") // placeholder
  TO:   .requestIdToken("YOUR_REAL_WEB_CLIENT_ID.apps.googleusercontent.com")

---

## Security Score Track karo

Before audit:           62/100
After today quick fixes: ~72/100
After STEP 1+2+3:       ~88/100
After ALL steps:         ~95/100

---
Reminder created: 2026-08-04
