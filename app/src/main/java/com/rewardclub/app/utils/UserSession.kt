// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Serializable
data class DbProfile(
    val id: String,
    val email: String,
    val full_name: String? = "",
    val mobile: String? = "",
    val total_coins: Long? = 0L,
    val redeemed_coins: Long? = 0L,
    val total_savings: Long? = 0L,
    val pending_coins: Long? = 0L,
    val withdrawn_coins: Long? = 0L
)

@Serializable
data class DbWithdrawal(
    val user_id: String,
    val user_name: String,
    val user_email: String,
    val user_mobile: String,
    val amount_inr: Double,
    val coins_deducted: Long,
    val payment_method: String = "UPI",
    val payout_details: String,
    val status: String = "pending"
)

object UserSession {
    private const val PREFS_NAME = "reward_club_user_session"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_UID = "uid"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"
    private const val KEY_MOBILE = "mobile"
    private const val KEY_TOTAL_COINS = "total_coins"
    private const val KEY_WITHDRAWN_COINS = "withdrawn_coins"
    private const val KEY_PENDING_COINS = "pending_coins"
    private const val KEY_TOTAL_SAVINGS = "total_savings"

    private var appContext: Context? = null

    var currentUser by mutableStateOf<MockUser?>(null)
        private set

    var isSessionChecked by mutableStateOf(false)
        private set

    var isGuest by mutableStateOf(true)

    var totalCoins by mutableStateOf(0L)
    var pendingCoins by mutableStateOf(0L)
    var withdrawnCoins by mutableStateOf(0L)
    var redeemedCoins by mutableStateOf(0L)
    var totalSavings by mutableStateOf(0L)
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var mobile by mutableStateOf("")

    data class MockUser(val uid: String, val email: String, val displayName: String)

    private val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Initializes UserSession with Application Context and immediately restores any saved session.
     */
    fun init(context: Context) {
        appContext = context.applicationContext
        loadPersistedSession()
    }

    private fun getPrefs(): SharedPreferences? {
        return appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun loadPersistedSession() {
        val prefs = getPrefs()
        if (prefs != null && prefs.getBoolean(KEY_IS_LOGGED_IN, false)) {
            val savedUid = prefs.getString(KEY_UID, "") ?: ""
            val savedEmail = prefs.getString(KEY_EMAIL, "") ?: ""
            val savedName = prefs.getString(KEY_NAME, "") ?: ""
            val savedMobile = prefs.getString(KEY_MOBILE, "") ?: ""

            if (savedUid.isNotEmpty() && savedEmail.isNotEmpty()) {
                val cleanEmail = savedEmail.trim().lowercase()
                val resolvedName = savedName.ifEmpty { cleanEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User" }
                
                currentUser = MockUser(
                    uid = savedUid,
                    email = cleanEmail,
                    displayName = resolvedName
                )
                email = cleanEmail
                fullName = resolvedName
                mobile = savedMobile
                totalCoins = prefs.getLong(KEY_TOTAL_COINS, 0L)
                withdrawnCoins = prefs.getLong(KEY_WITHDRAWN_COINS, 0L)
                redeemedCoins = withdrawnCoins
                pendingCoins = prefs.getLong(KEY_PENDING_COINS, 0L)
                totalSavings = prefs.getLong(KEY_TOTAL_SAVINGS, 0L)
                isGuest = false

                // Refresh latest profile & coin balance in background
                sessionScope.launch {
                    fetchProfileAndStats(savedUid)
                }
            }
        }
        isSessionChecked = true
    }

    private fun persistSession() {
        val user = currentUser
        val prefs = getPrefs() ?: return
        if (user != null) {
            prefs.edit().apply {
                putBoolean(KEY_IS_LOGGED_IN, true)
                putString(KEY_UID, user.uid)
                putString(KEY_EMAIL, email)
                putString(KEY_NAME, fullName)
                putString(KEY_MOBILE, mobile)
                putLong(KEY_TOTAL_COINS, totalCoins)
                putLong(KEY_WITHDRAWN_COINS, withdrawnCoins)
                putLong(KEY_PENDING_COINS, pendingCoins)
                putLong(KEY_TOTAL_SAVINGS, totalSavings)
                apply()
            }
        } else {
            clearPersistedSession()
        }
    }

    private fun clearPersistedSession() {
        getPrefs()?.edit()?.clear()?.apply()
    }

    // Listen to Supabase auth status changes
    suspend fun listenToSession() {
        Supabase.client.auth.sessionStatus.collectLatest { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val session = status.session
                    val user = session.user
                    if (user != null) {
                        val cleanEmail = user.email ?: ""
                        val defaultName = cleanEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User"
                        currentUser = MockUser(
                            uid = user.id,
                            email = cleanEmail,
                            displayName = defaultName
                        )
                        email = cleanEmail
                        fullName = defaultName
                        isGuest = false
                        persistSession()
                        fetchProfileAndStats(user.id)
                    }
                    isSessionChecked = true
                }
                is SessionStatus.NotAuthenticated -> {
                    // Only clear if no local persistent session exists
                    val prefs = getPrefs()
                    val hasLocalSession = prefs?.getBoolean(KEY_IS_LOGGED_IN, false) == true
                    if (!hasLocalSession && currentUser == null) {
                        clearSession()
                    }
                    isSessionChecked = true
                }
                else -> {
                    // e.g. SessionStatus.Loading or SessionStatus.NetworkError
                }
            }
        }
    }

    suspend fun fetchProfileAndStats(userId: String) {
        try {
            // Fetch Profile from Supabase
            var profile = Supabase.client.postgrest["profiles"]
                .select { filter { eq("id", userId) } }
                .decodeSingleOrNull<DbProfile>()
            
            if (profile == null && currentUser != null) {
                try {
                    val newP = DbProfile(
                        id = userId,
                        email = email,
                        full_name = fullName,
                        mobile = mobile,
                        total_coins = 0L,
                        redeemed_coins = 0L,
                        total_savings = 0L
                    )
                    Supabase.client.postgrest["profiles"].upsert(newP)
                    profile = newP
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (profile != null) {
                if (!profile.full_name.isNullOrEmpty()) {
                    fullName = profile.full_name
                }
                if (!profile.mobile.isNullOrEmpty()) {
                    mobile = profile.mobile
                }
                totalCoins = profile.total_coins ?: 0L
                withdrawnCoins = profile.withdrawn_coins ?: profile.redeemed_coins ?: 0L
                pendingCoins = profile.pending_coins ?: (totalCoins - withdrawnCoins).coerceAtLeast(0L)
                redeemedCoins = withdrawnCoins
                totalSavings = profile.total_savings ?: (totalCoins / 4L)
                currentUser = currentUser?.copy(displayName = fullName)

                // Persist fresh stats
                persistSession()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun login(userEmail: String, uid: String, name: String = "", userMobile: String = "") {
        val cleanEmail = userEmail.trim().lowercase()
        val defaultName = name.ifEmpty { cleanEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User" }
        
        currentUser = MockUser(
            uid = uid,
            email = cleanEmail,
            displayName = defaultName
        )
        email = cleanEmail
        fullName = defaultName
        mobile = userMobile
        isGuest = false
        isSessionChecked = true

        // Persist immediately to SharedPreferences
        persistSession()

        // Attempt async fetch of database profile
        sessionScope.launch {
            fetchProfileAndStats(uid)
        }
    }

    suspend fun requestUpiWithdrawal(upiId: String, coins: Long): Boolean {
        return try {
            val amountInr = (coins / 4.0)
            val user = currentUser
            val uid = user?.uid ?: "guest_${System.currentTimeMillis()}"
            val record = DbWithdrawal(
                user_id = uid,
                user_name = fullName.ifEmpty { "Reward Club Member" },
                user_email = email.ifEmpty { "member@rewardclub.com" },
                user_mobile = mobile.ifEmpty { "Not Provided" },
                amount_inr = amountInr,
                coins_deducted = coins,
                payment_method = "UPI",
                payout_details = upiId.trim(),
                status = "pending"
            )
            try {
                Supabase.client.postgrest["withdrawals"].insert(record)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pendingCoins = (pendingCoins - coins).coerceAtLeast(0L)
            withdrawnCoins = withdrawnCoins + coins
            redeemedCoins = withdrawnCoins
            persistSession()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun logout() {
        sessionScope.launch {
            try {
                Supabase.client.auth.signOut()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        isGuest = false
        clearPersistedSession()
        clearSession()
    }

    fun updateProfile(name: String, phone: String, onComplete: (Boolean) -> Unit) {
        fullName = name
        mobile = phone
        persistSession()
        val userId = currentUser?.uid
        if (userId != null) {
            sessionScope.launch {
                try {
                    Supabase.client.postgrest["profiles"].update(
                        {
                            set("full_name", name)
                            set("mobile", phone)
                        }
                    ) {
                        filter { eq("id", userId) }
                    }
                    currentUser = currentUser?.copy(displayName = name)
                    persistSession()
                    onComplete(true)
                } catch (e: Exception) {
                    e.printStackTrace()
                    onComplete(false)
                }
            }
        } else {
            onComplete(true)
        }
    }

    private fun clearSession() {
        currentUser = null
        email = ""
        fullName = ""
        mobile = ""
        totalCoins = 0L
        pendingCoins = 0L
        withdrawnCoins = 0L
        redeemedCoins = 0L
        totalSavings = 0L
    }
}
