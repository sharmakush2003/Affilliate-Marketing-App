// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
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
import androidx.compose.material.icons.filled.ArrowBack
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
                if (account != null) {
                    val email = account.email ?: "google-user@rewardclub.com"
                    val name = account.displayName ?: "Google User"
                    com.rewardclub.app.utils.UserSession.login(email, name)
                    Toast.makeText(context, "Welcome, $name!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                if (com.rewardclub.app.BuildConfig.DEBUG) {
                    android.util.Log.e("LoginScreen", "Google Sign-In failed: ${e.statusCode}", e)
                }
                Toast.makeText(context, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val density = LocalDensity.current.density

    // 3D rotation infinite transition angle
    val infiniteTransition = rememberInfiniteTransition(label = "3D_Transition")
    val rotationY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationY"
    )

    // Countdown Timer logic
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdownTime = 30
            while (countdownTime > 0) {
                delay(1000)
                countdownTime--
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sign In / Sign Up", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = White,
                        titleContentColor = TextDark,
                        navigationIconContentColor = TextDark
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrayBackground)
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Elegant top gradient header box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(DarkGreen, DarkGreen.copy(alpha = 0.8f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pulsing outer ring around Y-axis spinning golden 3D coin
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(90.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .graphicsLayer {
                                        this.rotationZ = -rotationY
                                    }
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.sweepGradient(
                                            colors = listOf(AccentGold, Color.Transparent, AccentGold, Color.Transparent)
                                        ),
                                        shape = CircleShape
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .graphicsLayer {
                                        this.rotationY = rotationY
                                        cameraDistance = 12f * density
                                    }
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = com.rewardclub.app.R.drawable.reward_club_logo),
                                    contentDescription = "Reward Club Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Text(
                            text = "Reward Club",
                            color = White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "aha, everywhere!",
                            color = AccentGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Main input card
                Card(
                    colors = CardDefaults.cardColors(containerColor = White),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, BorderColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Text(
                            text = if (!isOtpSent) "Enter Email Address" else "Verify Email OTP",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        // Custom Premium Email Field
                        BasicTextField(
                            value = emailAddress,
                            onValueChange = { if (!isOtpSent) emailAddress = it },
                            enabled = !isOtpSent,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(
                                    color = if (isOtpSent) Color(0xFFF5F7FA) else White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = if (isEmailFocused && !isOtpSent) 2.dp else 1.dp,
                                    color = if (isEmailFocused && !isOtpSent) DarkGreen else BorderColor,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .onFocusChanged { isEmailFocused = it.isFocused },
                            decorationBox = { innerTextField ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = if (isEmailFocused && !isOtpSent) DarkGreen else TextGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(BorderColor)
                                    )
                                    
                                    Box(modifier = Modifier.weight(1f)) {
                                        if (emailAddress.isEmpty()) {
                                            Text("Enter your email address", color = TextGray, fontSize = 14.sp)
                                        }
                                        innerTextField()
                                    }
                                }
                            }
                        )

                        // GET OTP Button
                        if (!isOtpSent) {
                            Button(
                                onClick = {
                                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                                    if (emailRegex.matches(emailAddress.trim())) {
                                        otpAttempts = 0
                                        isSendingEmail = true
                                        Toast.makeText(context, "Sending OTP to $emailAddress...", Toast.LENGTH_SHORT).show()
                                        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                            val result = OtpApiClient.sendOtp(emailAddress.trim())
                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                isSendingEmail = false
                                                if (result.success) {
                                                    isOtpSent = true
                                                    Toast.makeText(context, "OTP Sent to $emailAddress!", Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues(),
                                enabled = !isSendingEmail,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(4.dp, RoundedCornerShape(12.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(DarkGreen, Color(0xFF2E7D32))
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                if (isSendingEmail) {
                                    CircularProgressIndicator(
                                        color = White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text("GET OTP", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                                }
                            }
                        }

                        // OTP verification section - Slides open directly below the email input
                        AnimatedVisibility(
                            visible = isOtpSent,
                            enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                            exit = shrinkVertically(animationSpec = tween(300)) + fadeOut()
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "OTP sent to email: $emailAddress",
                                    fontSize = 12.sp,
                                    color = TextGray
                                )

                                // Interactive OTP inputs
                                BasicTextField(
                                    value = otpCode,
                                    onValueChange = {
                                        if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                            otpCode = it
                                        }
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth().height(56.dp),
                                    decorationBox = {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            repeat(4) { idx ->
                                                val char = otpCode.getOrNull(idx)?.toString() ?: ""
                                                val isFocused = otpCode.length == idx
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(56.dp)
                                                        .background(Color(0xFFF5F7FA), shape = RoundedCornerShape(8.dp))
                                                        .border(
                                                            width = if (isFocused) 2.dp else 1.dp,
                                                            color = if (isFocused) DarkGreen else BorderColor,
                                                            shape = RoundedCornerShape(8.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = char,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextDark
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )

                                Button(
                                    onClick = {
                                        isSendingEmail = true
                                        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                            val result = OtpApiClient.verifyOtp(emailAddress.trim(), otpCode)
                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                if (result.verified) {
                                                    isSendingEmail = false
                                                    com.rewardclub.app.utils.UserSession.login(emailAddress.trim())
                                                    Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                                                    onLoginSuccess()
                                                } else {
                                                    isSendingEmail = false
                                                    Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
                                                    if (result.remaining == 0) {
                                                        otpCode = ""
                                                        isOtpSent = false
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                    contentPadding = PaddingValues(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .shadow(4.dp, RoundedCornerShape(12.dp))
                                        .background(
                                            brush = Brush.horizontalGradient(
                                                colors = listOf(DarkGreen, Color(0xFF2E7D32))
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Text("VERIFY & PROCEED", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                                }

                                // Timer / Resend OTP Action
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Wrong email?",
                                        color = DarkGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            otpCode = ""
                                            isOtpSent = false
                                        }
                                    )

                                    if (countdownTime > 0) {
                                        Text(
                                            text = "Resend OTP in ${countdownTime}s",
                                            fontSize = 12.sp,
                                            color = TextGray
                                        )
                                    } else {
                                        Text(
                                            text = "Resend OTP",
                                            fontSize = 12.sp,
                                            color = DarkGreen,
                                            fontWeight = FontWeight.Bold,
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
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Social Login layout
                Text("Or continue with", color = TextGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // Premium Google Login Pill Button
                Box(
                    modifier = Modifier
                        .width(240.dp)
                        .height(48.dp)
                        .background(White, shape = RoundedCornerShape(24.dp))
                        .border(1.dp, BorderColor, RoundedCornerShape(24.dp))
                        .shadow(2.dp, RoundedCornerShape(24.dp))
                        .clickable {
                            val signInIntent = googleSignInClient.signInIntent
                            signInLauncher.launch(signInIntent)
                        }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "G",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEA4335)
                        )
                        Text(
                            text = "Continue with Google",
                            color = TextDark,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
