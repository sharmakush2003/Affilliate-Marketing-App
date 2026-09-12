# 🚀 Reward Club — Affiliate Marketing Ecosystem

Ye repository ek **Monorepo** hai jisme 3 mukhya components hain:

---

## 📁 Project Architecture & Folder Breakdown

```text
Affilliate-Marketing-App/
│
├── 📱 app/                       ─── [1. ANDROID MOBILE APP]
│   ├── src/main/java/           ─── App Source Code (Kotlin & Jetpack Compose)
│   ├── src/main/res/            ─── App UI Assets, Images, Layouts
│   └── build.gradle.kts         ─── Android App Build Configurations
│
├── 💻 admin-dashboard/           ─── [2. ADMIN WEB PANEL (Next.js)]
│   ├── app/                     ─── Frontend Pages & Backend API Routes
│   │   ├── dashboard/           ─── [Frontend] Admin Dashboard Screens
│   │   │   ├── users/           ─── User Management Screen
│   │   │   ├── transactions/    ─── Affiliate Transactions Tracker
│   │   │   ├── clicks/          ─── Real-time Click Tracker
│   │   │   ├── offers/          ─── CueLinks Offers & Deals Manager
│   │   │   └── sync/            ─── CueLinks Manual Sync Page
│   │   ├── login/               ─── [Frontend] Login & 2FA Verification Page
│   │   └── api/                 ─── [Backend API Routes]
│   │       ├── auth/            ─── Login, 2FA, OTP & Session Verification
│   │       ├── sync-cuelinks/   ─── Auto-sync CueLinks Transactions to Supabase
│   │       └── offers/          ─── CueLinks Offers API
│   ├── components/              ─── [Frontend] Reusable UI Components (Sidebar, Header, etc.)
│   ├── lib/                     ─── Supabase Client & Admin Auth Logic
│   └── .env.local               ─── Admin Panel Environment Secrets (Supabase, API Keys)
│
├── ⚡ supabase/                  ─── [3. BACKEND CLOUD & DATABASE]
│   └── functions/               ─── Supabase Edge Functions (e.g., send-support-email)
│
├── ⚙️ Gradle Build Files          ─── [Android App Build Tools]
│   ├── build.gradle.kts         ─── Root Gradle Build script
│   ├── settings.gradle.kts      ─── Project modules configuration
│   ├── gradle.properties        ─── Android JVM & Build properties
│   └── gradlew / gradlew.bat    ─── Gradle wrapper execution scripts
```

---

## 🛠️ Quick Commands

### 1. Admin Panel Run Karne Ke Liye:
```bash
cd admin-dashboard
npm run dev
```
👉 Open: `http://localhost:3000`

### 2. Android App Run Karne Ke Liye:
- Open the root directory in **Android Studio**
- Click **Run (Shift + F10)** to launch on an Emulator or Device.
