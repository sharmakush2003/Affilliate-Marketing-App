# Supabase Integration & Setup Guide

This guide explains how to connect your **Affiliate Marketing App** to **Supabase** (PostgreSQL) for Authentication (Email OTP & Google Sign-In) and database storage. 

By using Supabase, you don't need any custom backend servers (like Render.com) because Supabase handles Email OTP delivery and database queries natively.

---

## Step 1: Create a Supabase Project

1. Go to [Supabase](https://supabase.com/) and sign up.
2. Click **New Project** and select a name, database password, and region.
3. Once the project is created, go to **Project Settings -> API** (in the sidebar).
4. Copy the following keys:
   - **Project URL** (looks like `https://xxxxxx.supabase.co`)
   - **anon / public** API Key (long JWT token)

---

## Step 2: Configure Android Local Properties

Open your project's [local.properties](file:///c:/Users/kushs/OneDrive/Documents/App%20Development/Affilliate-Marketing-App/local.properties) file and append the following variables:

```properties
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=your-anon-public-key
```

---

## Step 3: Run Database Schema SQL

In your Supabase Dashboard:
1. Go to the **SQL Editor** in the sidebar.
2. Click **New Query**.
3. Paste the following SQL script to create your tables and enable Row-Level Security (RLS):

```sql
-- 1. Create User Profiles Table
CREATE TABLE public.profiles (
    id UUID REFERENCES auth.users(id) ON DELETE CASCADE PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    full_name TEXT DEFAULT '',
    mobile TEXT DEFAULT '',
    total_coins BIGINT DEFAULT 100, -- Default welcome coins
    redeemed_coins BIGINT DEFAULT 0,
    total_savings BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable Row-Level Security (RLS) on Profiles
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Allow anyone to insert profiles (during sign up)
CREATE POLICY "Allow public insert (sign up)" 
    ON public.profiles FOR INSERT 
    WITH CHECK (true);

-- Allow users to view only their own profile details
CREATE POLICY "Allow users to view own profile" 
    ON public.profiles FOR SELECT 
    USING (auth.uid() = id);

-- Allow users to update only their own profile details
CREATE POLICY "Allow users to update own profile" 
    ON public.profiles FOR UPDATE 
    USING (auth.uid() = id);

-- 2. Create Click Tracking Table
CREATE TABLE public.clicks (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    brand_name TEXT NOT NULL,
    click_time TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS on Clicks
ALTER TABLE public.clicks ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own clicks" 
    ON public.clicks FOR SELECT 
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own clicks" 
    ON public.clicks FOR INSERT 
    WITH CHECK (auth.uid() = user_id);

-- 3. Create Transactions Table (Coin Ledger)
CREATE TABLE public.transactions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    amount BIGINT NOT NULL,
    type TEXT NOT NULL, -- 'earn' or 'redeem'
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now()
);

-- Enable RLS on Transactions
ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Users can view their own transactions" 
    ON public.transactions FOR SELECT 
    USING (auth.uid() = user_id);

CREATE POLICY "Users can insert their own transactions" 
    ON public.transactions FOR INSERT 
    WITH CHECK (auth.uid() = user_id);
```

4. Click **Run** to execute the script.

---

## Step 4: Update Gradle Configuration

Open [app/build.gradle.kts](file:///c:/Users/kushs/OneDrive/Documents/App%20Development/Affilliate-Marketing-App/app/build.gradle.kts):

1. **Bump minSdk:** Change `minSdk = 24` to `minSdk = 26` (to support the Java time requirements of Supabase SDK).
2. **Add Build Config Fields:** Add build fields to read the keys from your `local.properties`:
   ```kotlin
   val supabaseUrl: String = localProperties.getProperty("SUPABASE_URL") ?: ""
   val supabaseAnonKey: String = localProperties.getProperty("SUPABASE_ANON_KEY") ?: ""
   buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
   buildConfigField("String", "SUPABASE_ANON_KEY", "\"$supabaseAnonKey\"")
   ```
3. **Add Dependencies:**
   ```kotlin
   dependencies {
       // Import the Supabase BOM
       implementation(platform("io.github.jan-tennert.supabase:bom:3.0.1"))
       
       // Add Supabase modules
       implementation("io.github.jan-tennert.supabase:postgrest-kt") // Database
       implementation("io.github.jan-tennert.supabase:auth-kt")       // Auth
       
       // Add Ktor engine (required for HTTP calls)
       implementation("io.ktor:ktor-client-android:3.0.0")
   }
   ```

---

## Step 5: Create the Supabase Client Singleton

Create a new file `app/src/main/java/com/rewardclub/app/utils/SupabaseClient.kt`:

```kotlin
package com.rewardclub.app.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

object Supabase {
    val client = createSupabaseClient(
        supabaseUrl = com.rewardclub.app.BuildConfig.SUPABASE_URL,
        supabaseKey = com.rewardclub.app.BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }
}
```

---

## Step 6: Connect Authentication in Kotlin

### 1. In `LoginScreen.kt`

#### For Email OTP Login:
Replace your button click triggers with the Supabase Auth APIs.

**To Send OTP:**
```kotlin
scope.launch(Dispatchers.IO) {
    try {
        Supabase.client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.OTP) {
            email = emailAddress.trim()
        }
        scope.launch(Dispatchers.Main) {
            isOtpSent = true
            Toast.makeText(context, "OTP Sent!", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
```

**To Verify OTP:**
```kotlin
scope.launch(Dispatchers.IO) {
    try {
        Supabase.client.auth.verifyEmailOtp(
            email = emailAddress.trim(),
            token = otpCode.trim(),
            type = io.github.jan.supabase.gotrue.OtpType.Email
        )
        // Retrieve profile and sync session
        val user = Supabase.client.auth.currentUserOrNull()
        if (user != null) {
            scope.launch(Dispatchers.Main) {
                UserSession.login(user.email ?: "", user.id)
                Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }
    } catch (e: Exception) {
        scope.launch(Dispatchers.Main) {
            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
```

#### For Google Login:
Retrieve the `idToken` from Google Sign-In as you currently do, and log in with Supabase:
```kotlin
scope.launch(Dispatchers.IO) {
    try {
        Supabase.client.auth.signInWith(io.github.jan.supabase.gotrue.providers.builtin.IDToken) {
            idToken = googleIdToken
            provider = io.github.jan.supabase.gotrue.providers.Google
        }
        val user = Supabase.client.auth.currentUserOrNull()
        if (user != null) {
            scope.launch(Dispatchers.Main) {
                UserSession.login(user.email ?: "", user.id)
                Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }
    } catch (e: Exception) {
        // Handle error...
    }
}
```

---

## Step 7: Connect Session Manager (`UserSession.kt`)

Update your `UserSession.kt` to load profiles and perform SQL aggregations:

```kotlin
package com.rewardclub.app.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable

@Serializable
data class DbProfile(
    val id: String,
    val email: String,
    val full_name: String? = "",
    val mobile: String? = ""
)

object UserSession {
    var userId by mutableStateOf("")
    var email by mutableStateOf("")
    var fullName by mutableStateOf("")
    var mobile by mutableStateOf("")
    
    var totalCoins by mutableStateOf(0L)
    var redeemedCoins by mutableStateOf(0L)
    var totalSavings by mutableStateOf(0L)

    var isLoggedIn by mutableStateOf(false)

    // Listen to session changes
    suspend fun listenToSession(scope: kotlinx.coroutines.CoroutineScope) {
        Supabase.client.auth.sessionFlow.collectLatest { session ->
            if (session != null) {
                val user = session.user
                if (user != null) {
                    userId = user.id
                    email = user.email ?: ""
                    isLoggedIn = true
                    fetchProfileAndStats()
                }
            } else {
                clearSession()
            }
        }
    }

    suspend fun fetchProfileAndStats() {
        if (userId.isEmpty()) return
        try {
            // 1. Fetch Profile
            val profile = Supabase.client.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingle<DbProfile>()
            
            fullName = profile.full_name ?: ""
            mobile = profile.mobile ?: ""

            // 2. Fetch Coin Totals using PostgreSQL aggregation
            // e.g. SUM of transaction amounts
            // val total = Supabase.client.postgrest["transactions"]...
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun login(userEmail: String, uid: String) {
        userId = uid
        email = userEmail
        isLoggedIn = true
    }

    fun logout() {
        io.github.jan.supabase.gotrue.auth.logout()
        clearSession()
    }

    private fun clearSession() {
        userId = ""
        email = ""
        fullName = ""
        mobile = ""
        totalCoins = 0
        redeemedCoins = 0
        totalSavings = 0
        isLoggedIn = false
    }
}
```
