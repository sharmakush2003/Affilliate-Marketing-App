// Â© 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.OtpApiClient
import com.rewardclub.app.utils.Supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.OtpType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isSendingEmail by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var emailAddress by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var countdownTime by remember { mutableStateOf(30) }
    var otpAttempts by remember { mutableStateOf(0) }
    val maxOtpAttempts = 5

    var isEmailFocused by remember { mutableStateOf(false) }
    var isNameFocused by remember { mutableStateOf(false) }
    var isSignInTab by remember { mutableStateOf(true) }
    var fullName by remember { mutableStateOf("") }

    // Configure Google Sign-In options
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("com.rewardclub.app") // TODO: replace with your actual Web Client ID from Supabase Dashboard
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember {
        GoogleSignIn.getClient(context, gso)
    }

    // Google Sign-In Launcher for OAuth activity result
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val googleIdToken = account?.idToken
                if (googleIdToken != null) {
                    val email = account.email ?: ""
                    val name = account.displayName ?: "Google User"
                    isSendingEmail = true
                    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                        try {
                            Supabase.client.auth.signInWith(IDToken) {
                                idToken = googleIdToken
                                provider = Google
                            }
                            val user = Supabase.client.auth.currentUserOrNull()
                            if (user != null) {
                                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                    isSendingEmail = false
                                    com.rewardclub.app.utils.UserSession.login(user.email ?: email, user.id, name)
                                    Toast.makeText(context, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                            } else {
                                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                    isSendingEmail = false
                                    Toast.makeText(context, "Failed to authenticate session with Supabase.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                isSendingEmail = false
                                Toast.makeText(context, "Supabase Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Google Sign-In: ID Token is null. Check Web Client ID configuration.", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                if (com.rewardclub.app.BuildConfig.DEBUG) {
                    android.util.Log.e("LoginScreen", "Google Sign-In failed: ${e.statusCode}", e)
                }
                Toast.makeText(context, "Google Sign-In failed: ${e.statusCode}. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF8FAFC))) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = TextDark
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Aesthetic Logo Placeholder
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(White, CircleShape)
                        .border(1.dp, BorderColor, CircleShape)
                        .shadow(4.dp, CircleShape, spotColor = Color(0x1A000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("RC", fontSize = 24.sp, fontWeight = FontWeight.Black, color = DarkGreen)
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Welcome to Reward Club",
                    color = TextDark,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to your account to continue",
                    color = TextGray,
                    fontSize = 15.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(40.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f) // Fill remaining space
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Custom Segmented Control
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .background(Color(0xFFF5F7FA), shape = RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        if (isSignInTab) White else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .then(if (isSignInTab) Modifier.shadow(2.dp, RoundedCornerShape(8.dp)) else Modifier)
                                    .clickable { isSignInTab = true; isOtpSent = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sign In", fontWeight = FontWeight.Bold, color = if (isSignInTab) TextDark else TextGray, fontSize = 14.sp)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .background(
                                        if (!isSignInTab) White else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .then(if (!isSignInTab) Modifier.shadow(2.dp, RoundedCornerShape(8.dp)) else Modifier)
                                    .clickable { isSignInTab = false; isOtpSent = false },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Sign Up", fontWeight = FontWeight.Bold, color = if (!isSignInTab) TextDark else TextGray, fontSize = 14.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(visible = !isSignInTab) {
                            Column {
                                Text("Full Name", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                                Spacer(modifier = Modifier.height(8.dp))
                                BasicTextField(
                                    value = fullName,
                                    onValueChange = { if (!isOtpSent) fullName = it },
                                    enabled = !isOtpSent,
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .background(color = White, shape = RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (isNameFocused && !isOtpSent) 2.dp else 1.dp,
                                            color = if (isNameFocused && !isOtpSent) DarkGreen else BorderColor,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .onFocusChanged { isNameFocused = it.isFocused },
                                    decorationBox = { innerTextField ->
                                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                if (fullName.isEmpty()) Text("John Doe", color = TextGray.copy(alpha=0.6f), fontSize = 15.sp)
                                                innerTextField()
                                            }
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Email Input
                        Text("Email address", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
                        Spacer(modifier = Modifier.height(8.dp))
                        BasicTextField(
                            value = emailAddress,
                            onValueChange = { if (!isOtpSent) emailAddress = it },
                            enabled = !isOtpSent,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .background(color = White, shape = RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isEmailFocused && !isOtpSent) 2.dp else 1.dp,
                                    color = if (isEmailFocused && !isOtpSent) DarkGreen else BorderColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .onFocusChanged { isEmailFocused = it.isFocused },
                            decorationBox = { innerTextField ->
                                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (emailAddress.isEmpty()) Text("name@example.com", color = TextGray.copy(alpha=0.6f), fontSize = 15.sp)
                                        innerTextField()
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                if (!isOtpSent) {
                    Button(
                        onClick = {
                            val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                            if (emailRegex.matches(emailAddress.trim())) {
                                otpAttempts = 0
                                isSendingEmail = true
                                Toast.makeText(context, "Sending OTP...", Toast.LENGTH_SHORT).show()
                                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                    try {
                                        Supabase.client.auth.signInWith(OTP) {
                                            email = emailAddress.trim()
                                        }
                                        scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                            isSendingEmail = false
                                            isOtpSent = true
                                        }
                                    } catch (e: Exception) {
                                        scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                            isSendingEmail = false
                                            Toast.makeText(context, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isSendingEmail,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (isSendingEmail) {
                            CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                        } else {
                            Text(if (isSignInTab) "Sign In" else "Create Account", color = White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // OTP verification section
                AnimatedVisibility(
                    visible = isOtpSent,
                    enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Enter the 6-digit code sent to your email",
                            fontSize = 14.sp,
                            color = TextGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive OTP inputs
                        BasicTextField(
                            value = otpCode,
                            onValueChange = {
                                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                                    otpCode = it
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            decorationBox = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    repeat(6) { idx ->
                                        val char = otpCode.getOrNull(idx)?.toString() ?: ""
                                        val isFocused = otpCode.length == idx
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(56.dp)
                                                .background(White, shape = RoundedCornerShape(8.dp))
                                                .border(
                                                    width = if (isFocused) 2.dp else 1.dp,
                                                    color = if (isFocused) DarkGreen else BorderColor,
                                                    shape = RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = char,
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDark
                                            )
                                        }
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                isSendingEmail = true
                                scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                    try {
                                        Supabase.client.auth.verifyEmailOtp(
                                            email = emailAddress.trim(),
                                            token = otpCode.trim(),
                                            type = OtpType.Email.MAGIC_LINK
                                        )
                                        val user = Supabase.client.auth.currentUserOrNull()
                                        if (user != null) {
                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                isSendingEmail = false
                                                com.rewardclub.app.utils.UserSession.login(user.email ?: emailAddress.trim(), user.id, if (!isSignInTab && fullName.isNotBlank()) fullName.trim() else "User")
                                                Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                                                onLoginSuccess()
                                            }
                                        } else {
                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                isSendingEmail = false
                                                Toast.makeText(context, "Session retrieval failed.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: Exception) {
                                        scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                            isSendingEmail = false
                                            Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(52.dp)
                        ) {
                            Text("Verify Code", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Use different email",
                                color = DarkGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    otpCode = ""
                                    isOtpSent = false
                                }
                            )

                            if (countdownTime > 0) {
                                Text(
                                    text = "Resend in ${countdownTime}s",
                                    fontSize = 13.sp,
                                    color = TextGray
                                )
                            } else {
                                Text(
                                    text = "Resend Code",
                                    fontSize = 13.sp,
                                    color = DarkGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.clickable {
                                        otpCode = ""
                                        isOtpSent = false
                                        countdownTime = 30
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Divider
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                    Text("OR", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Standard Google Button
                Button(
                    onClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        signInLauncher.launch(signInIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEA4335)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = TextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                    }
                }
            }
        }
    }
}
