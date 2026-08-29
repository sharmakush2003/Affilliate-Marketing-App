// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.utils

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
    val total_savings: Long? = 0L
)

object UserSession {
    var currentUser by mutableStateOf<MockUser?>(null)
        private set

    var isSessionChecked by mutableStateOf(false)
        private set

    var isGuest by mutableStateOf(false)

    var totalCoins by mutableStateOf(0L) 
    var redeemedCoins by mutableStateOf(0L)
    var totalSavings by mutableStateOf(0L)
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var mobile by mutableStateOf("")

    data class MockUser(val uid: String, val email: String, val displayName: String)

    private val sessionScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Listen to session changes
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
                        fetchProfileAndStats(user.id)
                    }
                    isSessionChecked = true
                }
                is SessionStatus.NotAuthenticated -> {
                    clearSession()
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
            // Fetch Profile
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
                mobile = profile.mobile ?: ""
                totalCoins = profile.total_coins ?: 0L
                redeemedCoins = profile.redeemed_coins ?: 0L
                totalSavings = profile.total_savings ?: 0L
                currentUser = currentUser?.copy(displayName = fullName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun login(userEmail: String, uid: String, name: String = "") {
        val cleanEmail = userEmail.trim().lowercase()
        val defaultName = name.ifEmpty { cleanEmail.split("@").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: "User" }
        
        currentUser = MockUser(
            uid = uid,
            email = cleanEmail,
            displayName = defaultName
        )
        email = cleanEmail
        fullName = defaultName
        mobile = ""
        totalCoins = 0L
        redeemedCoins = 0L
        totalSavings = 0L

        // Attempt async fetch of database profile
        sessionScope.launch {
            fetchProfileAndStats(uid)
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
        clearSession()
    }

    fun updateProfile(name: String, phone: String, onComplete: (Boolean) -> Unit) {
        fullName = name
        mobile = phone
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
        redeemedCoins = 0L
        totalSavings = 0L
    }
}
