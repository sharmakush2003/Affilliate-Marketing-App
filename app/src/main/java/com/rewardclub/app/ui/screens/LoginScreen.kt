// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import com.rewardclub.app.utils.DbProfile
import com.rewardclub.app.utils.Supabase
import com.rewardclub.app.utils.UserSession
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
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
                        // Mobile number already exists, finish login directly!
                        completeLogin(resolvedUid, email, resolvedName, existingMobile)
                    } else {
                        // Prompt user to enter their 10-digit mobile number
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

    // Modal Dialog for Collecting 10-Digit Mobile Number
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
                    .padding(16.dp)
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
                            .background(Color(0xFFEFF6FF), CircleShape)
                            .border(1.5.dp, Color(0xFF2563EB).copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "📱", fontSize = 28.sp)
                    }

                    Text(
                        text = "Complete Your Profile",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Hello $pendingName! Please enter your 10-digit mobile number to complete your account registration.",
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Mobile Number",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        BasicTextField(
                            value = inputMobile,
                            onValueChange = { if (it.length <= 10 && it.all { char -> char.isDigit() }) inputMobile = it },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isMobileFocused) 2.dp else 1.dp,
                                    color = if (isMobileFocused) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .onFocusChanged { isMobileFocused = it.isFocused },
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = null,
                                        tint = if (isMobileFocused) Color(0xFF2563EB) else Color(0xFF94A3B8),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (inputMobile.isEmpty()) {
                                            Text("9876543210", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        )
                    }

                    Button(
                        onClick = {
                            if (inputMobile.trim().length < 10) {
                                Toast.makeText(context, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            completeLogin(pendingUid, pendingEmail, pendingName, inputMobile.trim())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("Save & Continue 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    // World-Class Centered Amazon/E-Commerce Sign-In Layout with Prominent ChittorTech Logo
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Main Top & Middle Section (Centered Alignment)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Stylish Brand Badge Header
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0066FF), Color(0xFF003899))
                            ),
                            CircleShape
                        )
                        .border(3.dp, Color(0xFF90CAF9), CircleShape)
                        .shadow(14.dp, CircleShape, spotColor = Color(0x400052CC)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🛍️", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Reward Club",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0052CC),
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Shop. Earn Coins. Redeem Rewards.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Centered Main Heading & Subtitle
                Text(
                    text = "Sign in or create account",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Fast, 1-tap secure Google sign-in. Join thousands of shoppers earning cashback coins daily.",
                    fontSize = 13.5.sp,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Main Full-Width Google Sign-In Button
                Button(
                    onClick = { startGoogleSignIn() },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0052CC),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color(0x330052CC))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("G", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF4285F4))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Continue with Google",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Clean Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                    Text(
                        text = "  OR  ",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE2E8F0))
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Guest Button
                OutlinedButton(
                    onClick = {
                        UserSession.isGuest = true
                        onLoginSuccess()
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF8FAFC)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Browse as Guest 🛍️",
                        color = Color(0xFF334155),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Prominent Official ChittorTech Security Badge Footer
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🔒 ", fontSize = 15.sp)
                        Text(
                            text = "Secured by",
                            color = Color(0xFF475569),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Large Prominent ChittorTech Logo Image
                    Image(
                        painter = painterResource(id = R.drawable.chittortech_logo),
                        contentDescription = "ChittorTech Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .height(42.dp)
                            .fillMaxWidth(0.65f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Encrypted Data Protection • All Rights Reserved",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
