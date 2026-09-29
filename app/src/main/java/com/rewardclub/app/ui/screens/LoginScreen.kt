// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.accounts.AccountManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.rewardclub.app.BuildConfig
import com.rewardclub.app.R
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.rewardclub.app.utils.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🔒 SECURITY FIX: Admin API is now called via server-side proxy.
// The Android app NEVER holds the Supabase Service Role Key.
// All user creation/profile upsert is done by the Next.js backend.
private val ADMIN_API_BASE = BuildConfig.ADMIN_API_BASE_URL  // e.g. "https://affilliate-marketing-app.vercel.app"

/**
 * Calls the secure server-side proxy to create/update the user in Supabase Auth
 * and upsert their profile row — WITHOUT exposing the Service Role Key in the APK.
 */
private suspend fun registerUserViaProxy(
    email: String,
    name: String,
    mobile: String
): Pair<Boolean, String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
    return@withContext try {
        val url = java.net.URL("$ADMIN_API_BASE/api/auth/google-user")
        val conn = url.openConnection() as java.net.HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.connectTimeout = 10000
        conn.readTimeout = 10000
        conn.doOutput = true

        val body = org.json.JSONObject().apply {
            put("email", email.trim().lowercase())
            put("fullName", name.trim())
            put("mobile", mobile.trim())
        }.toString()

        java.io.OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body) }

        val responseCode = conn.responseCode
        if (responseCode == 200 || responseCode == 201) {
            val respText = conn.inputStream.bufferedReader().use { it.readText() }
            val json = org.json.JSONObject(respText)
            val userId = json.optString("userId", "")
            Pair(true, userId)
        } else {
            val errText = conn.errorStream?.bufferedReader()?.use { it.readText() } ?: "Server error $responseCode"
            android.util.Log.e("LoginScreen", "Proxy error $responseCode: $errText")
            Pair(false, "")
        }
    } catch (e: Exception) {
        android.util.Log.e("LoginScreen", "Proxy call failed: ${e.message}")
        Pair(false, "")
    }
}

enum class AuthMode {
    SIGN_IN,
    SIGN_UP
}

private data class PromoPassCardData(
    val badgeIcon: String,
    val brandTitle: String,
    val passTier: String,
    val cardNumber: String,
    val cardholderLabel: String,
    val cardholderName: String,
    val bgGradient: List<Color>,
    val borderColor: Color,
    val shadowSpotColor: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color
)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var selectedAuthMode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    // Registration & Profile Input Dialog State
    var showRegisterDialog by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf("") }
    var inputName by remember { mutableStateOf("") }
    var inputMobile by remember { mutableStateOf("") }
    var isNameFocused by remember { mutableStateOf(false) }
    var isMobileFocused by remember { mutableStateOf(false) }

    // Account Not Found Notice Dialog State (for Sign In when account doesn't exist)
    var showAccountNotFoundDialog by remember { mutableStateOf(false) }
    var notFoundEmail by remember { mutableStateOf("") }
    var notFoundSuggestedName by remember { mutableStateOf("") }

    // Helper to finish login after registration / detail collection
    fun completeLogin(email: String, name: String, mobile: String) {
        isLoading = true
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val cleanEmail  = email.trim().lowercase()
            val cleanName   = name.trim().ifEmpty { cleanEmail.substringBefore("@") }
            val cleanMobile = mobile.trim()

            // 🔒 SECURE: Call server-side proxy — no Service Role Key in APK
            val (success, userId) = registerUserViaProxy(cleanEmail, cleanName, cleanMobile)

            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                isLoading = false
                showRegisterDialog = false

                if (success && userId.isNotEmpty()) {
                    UserSession.login(
                        userEmail  = cleanEmail,
                        uid        = userId,
                        name       = cleanName,
                        userMobile = cleanMobile
                    )
                    Toast.makeText(context, "Welcome, $cleanName! 🎉", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    // Proxy failed — still allow login locally so UX isn't broken
                    // Profile will sync on next app open via fetchProfileAndStats
                    val fallbackUid = java.util.UUID.nameUUIDFromBytes(cleanEmail.toByteArray()).toString()
                    UserSession.login(
                        userEmail  = cleanEmail,
                        uid        = fallbackUid,
                        name       = cleanName,
                        userMobile = cleanMobile
                    )
                    Toast.makeText(context, "Welcome, $cleanName! 🎉", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
            }
        }
    }

    // Process authenticated Google account
    fun handleGoogleAuth(email: String, nameSuggestion: String) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isEmpty()) {
            isLoading = false
            return
        }

        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            var existingUserFound = false
            var existingMobile = ""
            var existingFullName = ""

            try {
                val url = java.net.URL("$ADMIN_API_BASE/api/users?email=${java.net.URLEncoder.encode(cleanEmail, "UTF-8")}")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 7000
                conn.readTimeout = 7000
                if (conn.responseCode == 200) {
                    val respText = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(respText)
                    val profileArr = json.optJSONArray("users")
                    if (profileArr != null) {
                        for (i in 0 until profileArr.length()) {
                            val p = profileArr.getJSONObject(i)
                            val pEmail = p.optString("email", "").trim().lowercase()
                            if (pEmail == cleanEmail) {
                                existingUserFound = true
                                existingMobile = p.optString("mobile", "").trim()
                                existingFullName = p.optString("full_name", "").trim()
                                break
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginScreen", "Error checking existing user: ${e.message}")
            }

            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                isLoading = false

                if (selectedAuthMode == AuthMode.SIGN_IN) {
                    // ── SIGN IN MODE: User MUST already exist in the database! ──
                    if (existingUserFound) {
                        val resolvedName = existingFullName.ifEmpty { nameSuggestion }
                        if (existingMobile.isNotEmpty()) {
                            // User exists with complete profile -> Log in directly
                            completeLogin(cleanEmail, resolvedName, existingMobile)
                        } else {
                            // User exists but has no mobile stored yet -> prompt for mobile
                            pendingEmail = cleanEmail
                            inputName = resolvedName
                            inputMobile = ""
                            showRegisterDialog = true
                        }
                    } else {
                        // ❌ USER NOT REGISTERED! Do NOT sign in and do NOT auto-create account.
                        // Show explicit notice dialog as requested by user.
                        notFoundEmail = cleanEmail
                        notFoundSuggestedName = nameSuggestion
                        showAccountNotFoundDialog = true
                    }
                } else {
                    // ── CREATE ACCOUNT (SIGN UP) MODE ──
                    if (existingUserFound) {
                        // User already registered -> notify and sign them in
                        Toast.makeText(context, "Account already exists! Logging you in...", Toast.LENGTH_SHORT).show()
                        val resolvedName = existingFullName.ifEmpty { nameSuggestion }
                        completeLogin(cleanEmail, resolvedName, existingMobile)
                    } else {
                        // Brand new user -> Prompt for both Display Name and 10-Digit Mobile Number
                        pendingEmail = cleanEmail
                        inputName = nameSuggestion.ifEmpty { cleanEmail.substringBefore("@") }
                        inputMobile = ""
                        showRegisterDialog = true
                    }
                }
            }
        }
    }

    // Single Direct Account Manager Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentData = result.data
        val accountName = intentData?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            ?: intentData?.extras?.getString(AccountManager.KEY_ACCOUNT_NAME)

        if (!accountName.isNullOrEmpty()) {
            val email = accountName.trim().lowercase()
            val rawName = email.substringBefore("@")
            val name = rawName.replace(".", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
                .ifEmpty { "Member" }
            handleGoogleAuth(email, name)
        } else {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val email = (account.email ?: "").lowercase().trim()
                    val name = account.displayName ?: email.substringBefore("@").ifEmpty { "Member" }
                    handleGoogleAuth(email, name)
                } else {
                    isLoading = false
                }
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    fun startGoogleSignIn() {
        if (isLoading) return
        isLoading = true
        try {
            val intent = AccountManager.newChooseAccountIntent(
                null, null, arrayOf("com.google"), true, null, null, null, null
            )
            googleSignInLauncher.launch(intent)
        } catch (e: Exception) {
            try {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build()
                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                googleSignInLauncher.launch(googleSignInClient.signInIntent)
            } catch (ex: Exception) {
                isLoading = false
                Toast.makeText(context, "Please select your Google account", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ── Notice Dialog: Account Not Found (Sign-In Block) ───────────────────
    if (showAccountNotFoundDialog) {
        Dialog(
            onDismissRequest = {
                showAccountNotFoundDialog = false
                isLoading = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFFED7AA)),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFFFF7ED), CircleShape)
                            .border(1.5.dp, Color(0xFFF97316).copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚠️", fontSize = 26.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Account Not Found",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Aapka account registered nahi hai. Kripya pehle register karein, tabhi sign in hoga.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }

                    // Email pill badge
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = notFoundEmail,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showAccountNotFoundDialog = false
                                selectedAuthMode = AuthMode.SIGN_UP
                                pendingEmail = notFoundEmail
                                inputName = notFoundSuggestedName.ifEmpty { notFoundEmail.substringBefore("@") }
                                inputMobile = ""
                                showRegisterDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Create Account",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                showAccountNotFoundDialog = false
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 44.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Cancel",
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Registration & Profile Details Dialog (Display Name + Mobile) ──────
    if (showRegisterDialog) {
        Dialog(
            onDismissRequest = {
                showRegisterDialog = false
                isLoading = false
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Color(0xFFEFF6FF), CircleShape)
                            .border(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "✨", fontSize = 24.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (selectedAuthMode == AuthMode.SIGN_UP) "Complete Your Profile" else "Link Mobile Number",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Please enter your display name and 10-digit mobile number for coin rewards & instant payouts.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }

                    // Email read-only badge
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📧", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = pendingEmail,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Field 1: Display Name
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Display Name / Full Name",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(
                                    width = if (isNameFocused) 1.5.dp else 1.dp,
                                    color = if (isNameFocused) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isNameFocused) Color(0xFF2563EB) else Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = inputName,
                                onValueChange = { inputName = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color(0xFF0F172A), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold
                                ),
                                cursorBrush = SolidColor(Color(0xFF2563EB)),
                                modifier = Modifier.weight(1f).onFocusChanged { isNameFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (inputName.isEmpty()) {
                                            Text(text = "e.g. Kush Sharma", color = Color(0xFF94A3B8), fontSize = 13.5.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }

                    // Field 2: Mobile Number
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Mobile Number (for UPI & Withdrawals)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(
                                    width = if (isMobileFocused) 1.5.dp else 1.dp,
                                    color = if (isMobileFocused) Color(0xFF059669) else Color(0xFFCBD5E1),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = "🇮🇳 +91", color = Color(0xFF0F172A), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = inputMobile,
                                onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) inputMobile = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color(0xFF0F172A), fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold
                                ),
                                cursorBrush = SolidColor(Color(0xFF059669)),
                                modifier = Modifier.weight(1f).onFocusChanged { isMobileFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (inputMobile.isEmpty()) {
                                            Text(text = "Enter 10-digit number", color = Color(0xFF94A3B8), fontSize = 13.5.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Button(
                        onClick = {
                            val cleanName = inputName.trim()
                            val cleanMobile = inputMobile.trim()
                            if (cleanName.length < 2) {
                                Toast.makeText(context, "Please enter your name", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (cleanMobile.length != 10) {
                                Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            completeLogin(pendingEmail, cleanName, cleanMobile)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 50.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Save & Continue",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = {
                            showRegisterDialog = false
                            isLoading = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 38.dp)
                    ) {
                        Text(text = "Cancel", color = Color(0xFF94A3B8), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // ── SOBER & ATTRACTIVE PREMIUM FINTECH AUTH SCREEN ─────────────────

    val promoCards = remember {
        listOf(
            PromoPassCardData(
                badgeIcon = "👑",
                brandTitle = "REWARD CLUB",
                passTier = "PRIVILEGE PASS",
                cardNumber = "••••  ••••  ••••  2026",
                cardholderLabel = "CARDHOLDER",
                cardholderName = "VALUED MEMBER",
                bgGradient = listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE), Color(0xFFBAE6FD)),
                borderColor = Color(0xFF7DD3FC),
                shadowSpotColor = Color(0x300284C7),
                primaryTextColor = Color(0xFF0C4A6E),
                secondaryTextColor = Color(0xFF0284C7)
            ),
            PromoPassCardData(
                badgeIcon = "🛍️",
                brandTitle = "REWARD CLUB",
                passTier = "CASHBACK ELITE",
                cardNumber = "••••  ••••  ••••  8899",
                cardholderLabel = "EXCLUSIVE ACCESS",
                cardholderName = "TOP BRAND DEALS",
                bgGradient = listOf(Color(0xFFFFF1F2), Color(0xFFFCE7F3), Color(0xFFFBCFE8)),
                borderColor = Color(0xFFF472B6),
                shadowSpotColor = Color(0x30DB2777),
                primaryTextColor = Color(0xFF831843),
                secondaryTextColor = Color(0xFFDB2777)
            ),
            PromoPassCardData(
                badgeIcon = "⚡",
                brandTitle = "REWARD CLUB",
                passTier = "INSTANT PAYOUT",
                cardNumber = "••••  ••••  ••••  7733",
                cardholderLabel = "TRANSFERS",
                cardholderName = "ZERO COMMISSION",
                bgGradient = listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7), Color(0xFFBBF7D0)),
                borderColor = Color(0xFF86EFAC),
                shadowSpotColor = Color(0x3016A34A),
                primaryTextColor = Color(0xFF14532D),
                secondaryTextColor = Color(0xFF16A34A)
            )
        )
    }

    val cardPagerState = rememberPagerState(pageCount = { promoCards.size })

    // Auto rotate every 5 seconds
    LaunchedEffect(cardPagerState) {
        while (true) {
            delay(5000L)
            val nextPage = (cardPagerState.currentPage + 1) % promoCards.size
            cardPagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDF8), // Luxury Ivory Cream
                        Color(0xFFFAF6EE), // Soft Warm Cream
                        Color(0xFFF8FAFC), // Off-white / Pearl Slate
                        Color(0xFFF1F5F9)  // Clean Subtle Tint
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, Color(0xFFE2E8F0), CircleShape)
                            .shadow(2.dp, CircleShape)
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Continue as Guest badge
                    Surface(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Explore as Guest",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569)
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Clean, elegant logo badge (Compact & Sober)
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF1E6D0), RoundedCornerShape(16.dp))
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = Color(0x10000000))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reward_club_logo),
                        contentDescription = "Reward Club Logo",
                        modifier = Modifier.fillMaxHeight(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Modern Segmented Control: Sign In / Sign Up
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(23.dp))
                        .background(Color(0xFFE2E8F0).copy(alpha = 0.7f))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedAuthMode == AuthMode.SIGN_IN) Color.White else Color.Transparent)
                            .clickable { selectedAuthMode = AuthMode.SIGN_IN }
                            .then(
                                if (selectedAuthMode == AuthMode.SIGN_IN) Modifier.shadow(2.dp, RoundedCornerShape(20.dp))
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign In",
                            fontSize = 13.5.sp,
                            fontWeight = if (selectedAuthMode == AuthMode.SIGN_IN) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedAuthMode == AuthMode.SIGN_IN) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedAuthMode == AuthMode.SIGN_UP) Color.White else Color.Transparent)
                            .clickable { selectedAuthMode = AuthMode.SIGN_UP }
                            .then(
                                if (selectedAuthMode == AuthMode.SIGN_UP) Modifier.shadow(2.dp, RoundedCornerShape(20.dp))
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Create Account",
                            fontSize = 13.5.sp,
                            fontWeight = if (selectedAuthMode == AuthMode.SIGN_UP) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedAuthMode == AuthMode.SIGN_UP) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dynamic Header Text
                AnimatedContent(
                    targetState = selectedAuthMode,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(120))
                    },
                    label = "authHeaderTransition"
                ) { mode ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = if (mode == AuthMode.SIGN_IN) "Welcome Back" else "Join Reward Club",
                            fontSize = 23.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (mode == AuthMode.SIGN_IN)
                                "Sign in to access your coins, cashback deals & 1-tap payouts."
                            else
                                "Create your account in seconds to unlock cashback & high-yield coin rewards.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── 3 ROTATING SOBER VIRTUAL MEMBERSHIP PASS CARDS (Light Blue, Light Pink, Light Green) ──
                HorizontalPager(
                    state = cardPagerState,
                    pageSpacing = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                ) { pageIndex ->
                    val card = promoCards[pageIndex]
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(185.dp)
                            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = card.shadowSpotColor)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Brush.linearGradient(card.bgGradient))
                            .border(BorderStroke(1.2.dp, card.borderColor), RoundedCornerShape(22.dp))
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        // Subtle background radial watermark
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = 35.dp, y = 35.dp)
                                .background(
                                    Brush.radialGradient(
                                        listOf(card.borderColor.copy(alpha = 0.25f), Color.Transparent)
                                    ),
                                    CircleShape
                                )
                        )

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Card Top Row: Brand & NFC Wave
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color.White, card.borderColor.copy(alpha = 0.5f))
                                                ),
                                                CircleShape
                                            )
                                            .border(1.dp, card.borderColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(card.badgeIcon, fontSize = 14.sp)
                                    }
                                    Column {
                                        Text(
                                            text = card.brandTitle,
                                            color = card.primaryTextColor,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 1.2.sp
                                        )
                                        Text(
                                            text = card.passTier,
                                            color = card.secondaryTextColor,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                }

                                Text(
                                    text = "))))",
                                    color = card.secondaryTextColor,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Card Middle Row: Golden Metallic Chip & Number
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Metallic SIM Chip Graphic
                                Box(
                                    modifier = Modifier
                                        .size(width = 38.dp, height = 28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFFFDE68A), Color(0xFFD4AF37), Color(0xFFF59E0B))
                                            )
                                        )
                                        .border(0.8.dp, Color(0xFFB45309).copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(3.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        HorizontalDivider(color = Color(0xFF78350F).copy(alpha = 0.4f), thickness = 0.8.dp)
                                        HorizontalDivider(color = Color(0xFF78350F).copy(alpha = 0.4f), thickness = 0.8.dp)
                                    }
                                }

                                // Embossed Card Identifier
                                Text(
                                    text = card.cardNumber,
                                    color = card.primaryTextColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            }

                            // Card Bottom Row: Member Name & Tier Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Column {
                                    Text(
                                        text = card.cardholderLabel,
                                        color = card.secondaryTextColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = card.cardholderName,
                                        color = card.primaryTextColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Smooth Rotating Page Indicators (Dots)
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    repeat(promoCards.size) { index ->
                        val isSelected = cardPagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 22.dp else 6.dp,
                            animationSpec = tween(300),
                            label = "indicatorWidth"
                        )
                        val color = when {
                            isSelected && index == 0 -> Color(0xFF0284C7)
                            isSelected && index == 1 -> Color(0xFFDB2777)
                            isSelected && index == 2 -> Color(0xFF16A34A)
                            else -> Color(0xFFCBD5E1)
                        }
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(6.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // High-End Google Sign-In Button
                Button(
                    onClick = { startGoogleSignIn() },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0F172A)
                    ),
                    border = BorderStroke(1.2.dp, Color(0xFFCBD5E1)),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF2563EB),
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (selectedAuthMode == AuthMode.SIGN_IN) "Sign in with Google" else "Sign up with Google",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}


