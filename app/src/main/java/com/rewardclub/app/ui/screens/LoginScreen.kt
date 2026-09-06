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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.rewardclub.app.BuildConfig
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.UserSession
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    // Mobile Number Collection Popup State
    var showMobileDialog by remember { mutableStateOf(false) }
    var pendingEmail by remember { mutableStateOf("") }
    var pendingUid by remember { mutableStateOf("") }
    var pendingName by remember { mutableStateOf("") }
    var inputMobile by remember { mutableStateOf("") }
    var isMobileFocused by remember { mutableStateOf(false) }

    // Helper to finish login after mobile collection
    fun completeLogin(uid: String, email: String, name: String, mobile: String) {
        isLoading = true
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val cleanEmail = email.trim().lowercase()
            val cleanName = name.trim().ifEmpty { cleanEmail.substringBefore("@") }
            val cleanMobile = mobile.trim()

            var finalUid = uid
            val supabaseBaseUrl = BuildConfig.SUPABASE_URL
            val serviceRoleKey = BuildConfig.SUPABASE_SERVICE_ROLE_KEY

            // 1. Direct Cloud Call: Create/confirm user in Supabase Auth (auth.users)
            try {
                val url = java.net.URL("$supabaseBaseUrl/auth/v1/admin/users")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("apikey", serviceRoleKey)
                conn.setRequestProperty("Authorization", "Bearer $serviceRoleKey")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.doOutput = true

                val jsonBody = org.json.JSONObject().apply {
                    put("email", cleanEmail)
                    put("email_confirm", true)
                    put("user_metadata", org.json.JSONObject().apply {
                        put("full_name", cleanName)
                        put("name", cleanName)
                        put("mobile", cleanMobile)
                        put("phone", cleanMobile)
                    })
                }.toString()

                conn.outputStream.use { os ->
                    os.write(jsonBody.toByteArray(Charsets.UTF_8))
                }

                val respCode = conn.responseCode
                if (respCode == 200 || respCode == 201) {
                    val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = org.json.JSONObject(responseText)
                    val returnedId = json.optString("id", "")
                    if (returnedId.isNotEmpty()) {
                        finalUid = returnedId
                    }
                } else if (respCode == 422 || respCode == 400) {
                    // Update user metadata for existing user
                    try {
                        val getUrl = java.net.URL("$supabaseBaseUrl/auth/v1/admin/users")
                        val getConn = getUrl.openConnection() as java.net.HttpURLConnection
                        getConn.requestMethod = "GET"
                        getConn.setRequestProperty("apikey", serviceRoleKey)
                        getConn.setRequestProperty("Authorization", "Bearer $serviceRoleKey")
                        if (getConn.responseCode == 200) {
                            val respText = getConn.inputStream.bufferedReader().use { it.readText() }
                            val rootJson = org.json.JSONObject(respText)
                            val usersArray = rootJson.optJSONArray("users")
                            if (usersArray != null) {
                                for (i in 0 until usersArray.length()) {
                                    val u = usersArray.getJSONObject(i)
                                    if (u.optString("email", "").equals(cleanEmail, ignoreCase = true)) {
                                        finalUid = u.optString("id", finalUid)
                                        val updateUrl = java.net.URL("$supabaseBaseUrl/auth/v1/admin/users/$finalUid")
                                        val updateConn = updateUrl.openConnection() as java.net.HttpURLConnection
                                        updateConn.requestMethod = "PUT"
                                        updateConn.setRequestProperty("apikey", serviceRoleKey)
                                        updateConn.setRequestProperty("Authorization", "Bearer $serviceRoleKey")
                                        updateConn.setRequestProperty("Content-Type", "application/json")
                                        updateConn.doOutput = true
                                        val updateBody = org.json.JSONObject().apply {
                                            put("email_confirm", true)
                                            put("user_metadata", org.json.JSONObject().apply {
                                                put("full_name", cleanName)
                                                put("name", cleanName)
                                                put("mobile", cleanMobile)
                                                put("phone", cleanMobile)
                                            })
                                        }.toString()
                                        updateConn.outputStream.use { it.write(updateBody.toByteArray(Charsets.UTF_8)) }
                                        updateConn.responseCode
                                        break
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Direct Cloud Call: Upsert public.profiles database table using Service Role Key
            try {
                val url = java.net.URL("$supabaseBaseUrl/rest/v1/profiles?on_conflict=id")
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("apikey", serviceRoleKey)
                conn.setRequestProperty("Authorization", "Bearer $serviceRoleKey")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Prefer", "resolution=merge-duplicates")
                conn.doOutput = true

                val jsonBody = org.json.JSONObject().apply {
                    put("id", finalUid)
                    put("email", cleanEmail)
                    put("full_name", cleanName)
                    put("mobile", cleanMobile)
                    put("total_coins", 0)
                    put("redeemed_coins", 0)
                    put("total_savings", 0)
                }.toString()

                conn.outputStream.use { os ->
                    os.write(jsonBody.toByteArray(Charsets.UTF_8))
                }
                conn.responseCode
            } catch (e: Exception) {
                e.printStackTrace()
            }

            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                isLoading = false
                showMobileDialog = false
                UserSession.login(
                    userEmail = cleanEmail,
                    uid = finalUid,
                    name = cleanName,
                    userMobile = cleanMobile
                )
                Toast.makeText(context, "Welcome, $cleanName! 🎉", Toast.LENGTH_SHORT).show()
                onLoginSuccess()
            }
        }
    }

    // Process authenticated Google account
    fun handleGoogleAuth(email: String, name: String, uid: String) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val supabaseBaseUrl = BuildConfig.SUPABASE_URL
                val serviceRoleKey = BuildConfig.SUPABASE_SERVICE_ROLE_KEY
                var existingMobile = ""
                var resolvedName = name
                var resolvedUid = uid

                try {
                    val cleanEmail = email.trim().lowercase()
                    val url = java.net.URL("$supabaseBaseUrl/rest/v1/profiles?email=eq.$cleanEmail&select=*")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "GET"
                    conn.setRequestProperty("apikey", serviceRoleKey)
                    conn.setRequestProperty("Authorization", "Bearer $serviceRoleKey")
                    if (conn.responseCode == 200) {
                        val respText = conn.inputStream.bufferedReader().use { it.readText() }
                        val jsonArr = org.json.JSONArray(respText)
                        if (jsonArr.length() > 0) {
                            val profileObj = jsonArr.getJSONObject(0)
                            existingMobile = profileObj.optString("mobile", "")
                            resolvedName = profileObj.optString("full_name", name).ifEmpty { name }
                            resolvedUid = profileObj.optString("id", uid)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                    if (existingMobile.isNotEmpty()) {
                        completeLogin(resolvedUid, email, resolvedName, existingMobile)
                    } else {
                        isLoading = false
                        pendingUid = resolvedUid
                        pendingEmail = email
                        pendingName = resolvedName
                        inputMobile = ""
                        showMobileDialog = true
                    }
                }
            } catch (e: Exception) {
                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                    isLoading = false
                    pendingUid = uid
                    pendingEmail = email
                    pendingName = name
                    inputMobile = ""
                    showMobileDialog = true
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

            val uid = java.util.UUID.nameUUIDFromBytes(email.toByteArray()).toString()
            handleGoogleAuth(email, name, uid)
        } else {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(intentData)
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val email = (account.email ?: "").lowercase().trim()
                    val name = account.displayName ?: email.substringBefore("@").ifEmpty { "Member" }
                    val uid = java.util.UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                    handleGoogleAuth(email, name, uid)
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

    // Modal Dialog for Collecting 10-Digit Mobile Number (Clean White & Slate)
    if (showMobileDialog) {
        Dialog(
            onDismissRequest = {
                showMobileDialog = false
                isLoading = false
            }
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFFEFF6FF), CircleShape)
                            .border(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📱", fontSize = 26.sp)
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Link Your Mobile",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Welcome $pendingName! Please enter your 10-digit mobile number for 1-tap UPI coin withdrawals & order tracking.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 17.sp
                        )
                    }

                    // Clean Mobile Input with +91 Prefix
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
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
                            // Country Code Pill
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "🇮🇳 +91",
                                    color = Color(0xFF0F172A),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            BasicTextField(
                                value = inputMobile,
                                onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) inputMobile = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                cursorBrush = SolidColor(Color(0xFF059669)),
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { isMobileFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Box {
                                        if (inputMobile.isEmpty()) {
                                            Text(
                                                text = "Enter 10-digit number",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 13.5.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (inputMobile.trim().length < 10) {
                                Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            completeLogin(pendingUid, pendingEmail, pendingName, inputMobile.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "Save & Start Earning 🚀",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // ── PREMIUM FINTECH LOGIN SCREEN (Soft Light Blue Theme) ─────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE0F2FE),
                        Color(0xFFF0F7FF),
                        Color(0xFFE2EEFC)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // ── TOP BAR & BRAND LOGO ──────────────────────────────────────────
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row with optional back navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
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
                }

                // Official Reward Club Logo Card (Pure White with Subtle Shadow)
                Box(
                    modifier = Modifier
                        .height(76.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color(0x1A000000))
                        .padding(horizontal = 22.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reward_club_logo),
                        contentDescription = "Reward Club Logo",
                        modifier = Modifier.fillMaxHeight(),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Luxury Status Tag
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFEF3C7), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("👑", fontSize = 11.sp)
                        Text(
                            text = "PREMIER REWARDS & CASHBACK CLUB",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            letterSpacing = 0.6.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Headline
                Text(
                    text = "Welcome to Reward Club",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.3.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Earn up to ₹1,950 Coins per card approval & flat cash rewards on 28+ pre-approved top brands.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── 3-PILLAR BENEFIT STRIP (Clean White Elevated Cards) ───────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("💳 Cards", "Flat ₹1,950", "Instant approval"),
                        Triple("🛍️ Shop", "Upto 90 Coins", "Per ₹100 spend"),
                        Triple("⚡ UPI", "Bank Payout", "1-Tap transfer")
                    ).forEach { (cat, title, sub) ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(2.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF059669)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A),
                                    maxLines = 1
                                )
                                Text(
                                    text = sub,
                                    fontSize = 8.5.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── GOOGLE SIGN-IN BUTTON (Clean High-Contrast Card) ──────────
                Button(
                    onClick = { startGoogleSignIn() },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0F172A)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp), spotColor = Color(0x14000000))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFF059669),
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
                                text = "Continue with Google",
                                color = Color(0xFF0F172A),
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Clean Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                    Text(
                        text = "  OR  ",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFCBD5E1))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Guest Access Button (Clean White Outlined)
                OutlinedButton(
                    onClick = {
                        UserSession.isGuest = true
                        onLoginSuccess()
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🪙 ", fontSize = 14.sp)
                        Text(
                            text = "Explore Deals as Guest",
                            color = Color(0xFF334155),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── CHITTORTECH OFFICIAL SECURITY FOOTER (Clean White Card) ──────
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🔒 ", fontSize = 12.sp)
                        Text(
                            text = "256-Bit SSL Encrypted • Secured by",
                            color = Color(0xFF475569),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Image(
                        painter = painterResource(id = R.drawable.chittortech_logo),
                        contentDescription = "ChittorTech Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth(0.55f)
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Official Security Partner • All Rights Reserved",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
