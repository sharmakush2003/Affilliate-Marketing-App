// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UserSession {
    var currentUser by mutableStateOf<MockUser?>(null)
        private set

    var totalCoins by mutableStateOf(100L) // Default demo balance
    var redeemedCoins by mutableStateOf(50L)
    var totalSavings by mutableStateOf(150L)
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var mobile by mutableStateOf("")

    data class MockUser(val uid: String, val email: String, val displayName: String)

    fun login(userEmail: String, name: String = "") {
        val cleanEmail = userEmail.trim().lowercase()
        val defaultName = name.ifEmpty { cleanEmail.split("@").first().replaceFirstChar { it.uppercase() } }
        
        currentUser = MockUser(
            uid = "mock-uid-" + java.util.UUID.randomUUID().toString().substring(0, 8),
            email = cleanEmail,
            displayName = defaultName
        )
        email = cleanEmail
        fullName = defaultName
        mobile = ""
        totalCoins = 100L
        redeemedCoins = 50L
        totalSavings = 150L
    }

    fun logout() {
        currentUser = null
        email = ""
        fullName = ""
        mobile = ""
        totalCoins = 0L
        redeemedCoins = 0L
        totalSavings = 0L
    }

    fun updateProfile(name: String, phone: String, onComplete: (Boolean) -> Unit) {
        fullName = name
        mobile = phone
        onComplete(true)
    }
}
