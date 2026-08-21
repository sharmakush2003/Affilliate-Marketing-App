# Claude Sonnet Prompt: Personalized Affiliate Dashboard (Next.js + Supabase + CueLinks)

This document contains two parts:
1. **Architecture & Implementation Logic** (How the dashboard works and the critical database schemas).
2. **The Exact Claude Sonnet Prompt** (A premium, production-ready prompt you can copy-paste to Claude to generate the code).

---

## 1. Architecture & Integration Logic

Before building the dashboard, it is critical to understand how user clicks in your mobile app connect to conversions in the CueLinks dashboard and finally update Supabase.

### 🔗 Critical Change: SubID Click Tracking in Mobile App
Currently, when a user clicks a merchant link, your app generates a tracking link like:
`https://linksredirect.com/?cid=CHANNEL_ID&source=api&url=MERCHANT_URL`

To know **which user** made the purchase, you must append the **User's Supabase ID** as a `subid` parameter. Modify your link generation in [CuelinksApiService.kt](file:///c:/Users/kushs/OneDrive/Documents/App%20Development/Affilliate-Marketing-App/app/src/main/java/com/rewardclub/app/api/CuelinksApiService.kt) to:
```kotlin
fun createAffiliateLink(targetUrl: String, userId: String, channelId: String = CuelinksConfig.PUBLISHER_ID): String {
    val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
    // Appending subid with the unique user ID
    return "https://linksredirect.com/?cid=$channelId&source=api&subid=$userId&url=$encodedUrl"
}
```
*When a conversion is recorded on CueLinks, CueLinks will return this `userId` in the `sub_id` column of the transactions report.*

### 🗄️ Database Tables to Create in Supabase

To store transactions, run this SQL script in your **Supabase SQL Editor**:

```sql
-- Create transactions table
CREATE TABLE public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES public.profiles(id) ON DELETE SET NULL,
    cuelinks_transaction_id TEXT UNIQUE NOT NULL,
    campaign_name TEXT NOT NULL,
    merchant_name TEXT,
    order_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    commission_earned NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    cashback_credited_coins BIGINT NOT NULL DEFAULT 0,
    status TEXT NOT NULL CHECK (status IN ('pending', 'approved', 'rejected')),
    payment_status TEXT NOT NULL DEFAULT 'unpaid' CHECK (payment_status IN ('unpaid', 'paid')),
    click_time TIMESTAMP WITH TIME ZONE,
    transaction_time TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- Index for fast user transactions lookup
CREATE INDEX idx_transactions_user_id ON public.transactions(user_id);
```

---

## 2. Copy-Paste Claude Sonnet Prompt

Copy the entire block below and paste it into Claude Sonnet to generate your dashboard code:

```markdown
Create a highly professional, visually stunning, and secure Admin Dashboard in Next.js (App Router, React, Tailwind CSS) for an Affiliate Marketing & Cashback App. The dashboard needs to connect to our existing Supabase backend (which has 'profiles' and 'transactions' tables) and sync transaction logs from the CueLinks API.

### Technology Stack
- **Framework**: Next.js (App Router, Typescript)
- **Styling**: Tailwind CSS (sleek dark mode / premium glassmorphism aesthetic, outfit/inter fonts)
- **Icons**: Lucide React
- **Charts**: Recharts (smooth area/bar charts for metrics)
- **Database/Auth**: @supabase/supabase-js

---

### Dashboard Key Features & Pages

#### 1. Premium Landing & Login Page
- Clean, modern layout using absolute gradients and subtle glassmorphic forms.
- Safe Admin Authentication via Supabase Auth (Email & Password).

#### 2. Overview Dashboard (KPI Panel)
- **Metrics Cards**:
  - Total Registered Users
  - Total Clicks (Pings logged)
  - Total Sales/Order Volume (INR)
  - Total Commission Earned (CueLinks)
  - Total Cashback Paid/Unpaid to Users (Coins/INR)
- **Analytics Charts**:
  - Interactive Revenue & Transaction Count trends (daily/weekly/monthly).
  - Store Breakdown chart (e.g., Share of Amazon, Flipkart, Myntra sales).
- **Recent Activity Feed**:
  - Last 5 user registrations, last 5 tracked clicks/conversions.

#### 3. User Management Page
- Searchable & filterable table of users synced from Supabase 'profiles' table.
- Display Columns:
  - User ID, Name, Email, Mobile number.
  - Wallet balance stats: Total Coins, Redeemed Coins, Total Savings.
  - Creation Timestamp & Status.
- **Action Modal**: Clicking a user opens a modal showing their recent activity, click logs, and a manual override tool to manually credit/debit coins.

#### 4. Transaction Tracker (CueLinks Integration) Page
- Displays conversion records from the Supabase `transactions` table.
- Each row lists:
  - User Details (Name & Email, matching the transaction's `user_id` to `profiles.id`).
  - Store Name & Campaign Name.
  - Order Amount & Total Commission Earned.
  - Status Badge: `Pending` (Yellow), `Approved` (Green), `Rejected` (Red).
  - Payout Badge: `Paid` (Green) or `Unpaid` (Red).
- **Actions**:
  - Toggle payout status: Mark a transaction as "Paid" once bank transfer is done.
  - Recalculate and credit user cashback coins automatically based on commission approval.

#### 5. Integration Settings & Sync Panel
- **CueLinks Sync Trigger**: An interactive panel where the Admin can:
  - Configure `CUELINKS_API_KEY` and `PUBLISHER_ID` (saved in environment variables or a settings table).
  - Run an on-demand background sync.
  - Automate transaction fetching (outline a Next.js API Route for a Cron Job, like Vercel Cron, hitting `/api/sync-cuelinks`).
- **Sync Logic detail for the API route**:
  - Fetch transactions from CueLinks Endpoint (`GET https://api.cuelinks.com/v2/transactions.json`).
  - Read `sub_id` from each CueLinks transaction (which holds the user's Supabase UUID).
  - If a match is found in our Supabase `profiles` table:
    1. Upsert the transaction into our `transactions` table.
    2. If the CueLinks status changes to 'approved' and `payment_status` is 'unpaid', credit the corresponding cashback coins to the user's `total_coins` and `total_savings` in the `profiles` table.

---

### UI/UX & Styling Guidelines
- **Color Palette**: Sophisticated Dark Theme (Slate/Zinc slate backdrops, emerald green for earnings, violet/indigo gradients for accents, neon borders with thin drop shadows).
- **Design System**: Responsive layout with a sticky collapsible sidebar, smooth page-level transitions, loading skeletons, and interactive hover scales.
- **Tables**: Styled table elements featuring client-side pagination, search queries, filter tabs (by store, status, or date range), and CSV exports.

Provide the complete structure, key API routes (`/api/sync-cuelinks`), utility files for Supabase client initialization, and the core page components so we can drag and drop it into a fresh Next.js project.
```
