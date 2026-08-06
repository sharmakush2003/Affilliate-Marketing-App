// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object UserSession {
    var currentUser by mutableStateOf<FirebaseUser?>(null)
        private set

    var totalCoins by mutableStateOf(0L)
    var redeemedCoins by mutableStateOf(0L)
    var totalSavings by mutableStateOf(0L)
    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var mobile by mutableStateOf("")

    private var firestoreListener: ListenerRegistration? = null

    init {
        // Sync auth state on startup
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val user = auth.currentUser
            currentUser = user
            if (user != null) {
                email = user.email ?: ""
                // Start listening to Firestore user document
                startListeningToFirestore(user.uid)
            } else {
                stopListeningAndClear()
            }
        }
    }

    private fun startListeningToFirestore(uid: String) {
        firestoreListener?.remove()
        val docRef = FirebaseFirestore.getInstance().collection("users").document(uid)
        firestoreListener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                updateFromSnapshot(snapshot)
            } else {
                // If document doesn't exist, create it with default/initial values
                val initialData = hashMapOf(
                    "email" to (currentUser?.email ?: ""),
                    "fullName" to (currentUser?.displayName ?: "User"),
                    "mobile" to "",
                    "totalCoins" to 0L,
                    "redeemedCoins" to 0L,
                    "totalSavings" to 0L
                )
                docRef.set(initialData)
            }
        }
    }

    private fun updateFromSnapshot(snapshot: DocumentSnapshot) {
        fullName = snapshot.getString("fullName") ?: currentUser?.displayName ?: "User"
        email = snapshot.getString("email") ?: currentUser?.email ?: ""
        mobile = snapshot.getString("mobile") ?: ""
        totalCoins = snapshot.getLong("totalCoins") ?: 0L
        redeemedCoins = snapshot.getLong("redeemedCoins") ?: 0L
        totalSavings = snapshot.getLong("totalSavings") ?: 0L
    }

    private fun stopListeningAndClear() {
        firestoreListener?.remove()
        firestoreListener = null
        totalCoins = 0L
        redeemedCoins = 0L
        totalSavings = 0L
        fullName = ""
        email = ""
        mobile = ""
    }

    fun updateProfile(name: String, phone: String, onComplete: (Boolean) -> Unit) {
        val uid = currentUser?.uid ?: return
        val data = hashMapOf<String, Any>(
            "fullName" to name,
            "mobile" to phone
        )
        FirebaseFirestore.getInstance().collection("users").document(uid)
            .update(data)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}
