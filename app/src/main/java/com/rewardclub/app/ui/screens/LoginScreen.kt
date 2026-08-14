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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.DbProfile
import com.rewardclub.app.utils.Supabase
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.OTP
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class LoginStep {
    ENTER_EMAIL,
    ENTER_OTP
}

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
    var fullName by remember { mutableStateOf("") }

    var currentStep by remember { mutableStateOf(LoginStep.ENTER_EMAIL) }
    var countdownTime by remember { mutableStateOf(30) }

    var isEmailFocused by remember { mutableStateOf(false) }
    var isNameFocused by remember { mutableStateOf(false) }

    // Check if the user is in registration mode (rather than simple sign-in)
    var isRegistering by remember { mutableStateOf(false) }
    var showRegisterDialog by remember { mutableStateOf(false) }

    fun sendOtp(email: String) {
        scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                Supabase.client.auth.signInWith(OTP) {
                    this.email = email
                }
                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                    isSendingEmail = false
                    otpCode = ""
                    currentStep = LoginStep.ENTER_OTP
                }
            } catch (e: Exception) {
                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                    isSendingEmail = false
                    Toast.makeText(context, "Failed to send OTP: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Active countdown timer trigger when OTP step is active
    LaunchedEffect(currentStep) {
        if (currentStep == LoginStep.ENTER_OTP) {
            countdownTime = 30
            while (countdownTime > 0) {
                delay(1000L)
                countdownTime--
            }
        }
    }

    // Material 3 Dialog for Account Not Found Popup
    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = {
                Text(
                    text = "Account Not Found",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },
            text = {
                Text(
                    text = "We don't have an account registered with this email ID. Please register to create your account.",
                    fontSize = 14.sp,
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRegisterDialog = false
                        isRegistering = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Register Now", color = White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRegisterDialog = false }
                ) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Banner Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                if (isRegistering) Color(0xFFFFF3E0) else Color(0xFFE8F5E9),
                                Color(0xFFF8FAFC)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Floating circles
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .align(Alignment.TopStart)
                        .graphicsLayer(translationX = -80f, translationY = -80f)
                        .background(Color.White.copy(alpha = 0.35f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.BottomEnd)
                        .graphicsLayer(translationX = 40f, translationY = 40f)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(White, CircleShape)
                            .border(1.5.dp, if (isRegistering) AmazonOrange else DarkGreen, CircleShape)
                            .shadow(8.dp, CircleShape, spotColor = Color(0x1A000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isRegistering) "👋" else "🛍️", fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Reward Club",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = if (isRegistering) "Create your account today" else "Shop. Earn Coins. Redeem Rewards.",
                        fontSize = 13.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Main Card Box (Overlapping top banner slightly)
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                border = BorderStroke(1.dp, Color(0xFFECEFF1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .graphicsLayer(translationY = -20f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (currentStep) {
                        LoginStep.ENTER_EMAIL -> {
                            Text(
                                text = if (isRegistering) "Register / Sign Up" else "Sign In",
                                color = TextDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isRegistering) "Enter your details to create an account" else "Enter your email address to check your account",
                                color = TextGray,
                                fontSize = 13.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            // Name Input Field (Register mode only)
                            AnimatedVisibility(
                                visible = isRegistering,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Full Name",
                                        color = TextDark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    BasicTextField(
                                        value = fullName,
                                        onValueChange = { fullName = it },
                                        enabled = !isSendingEmail,
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp)
                                            .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                                            .border(
                                                width = if (isNameFocused) 2.dp else 1.dp,
                                                color = if (isNameFocused) DarkGreen else BorderColor,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .onFocusChanged { isNameFocused = it.isFocused },
                                        decorationBox = { innerTextField ->
                                            Row(
                                                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Person,
                                                    contentDescription = null,
                                                    tint = if (isNameFocused) DarkGreen else TextLight,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Box(modifier = Modifier.weight(1f)) {
                                                    if (fullName.isEmpty()) {
                                                        Text("John Doe", color = TextLight, fontSize = 15.sp)
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // Email Address input
                            Text(
                                text = "Email Address",
                                color = TextDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            BasicTextField(
                                value = emailAddress,
                                onValueChange = { emailAddress = it },
                                enabled = !isSendingEmail,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .background(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                                    .border(
                                        width = if (isEmailFocused) 2.dp else 1.dp,
                                        color = if (isEmailFocused) DarkGreen else BorderColor,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .onFocusChanged { isEmailFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Row(
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Email,
                                            contentDescription = null,
                                            tint = if (isEmailFocused) DarkGreen else TextLight,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (emailAddress.isEmpty()) {
                                                Text("name@example.com", color = TextLight, fontSize = 15.sp)
                                            }
                                            innerTextField()
                                        }
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                                    val cleanEmail = emailAddress.trim().lowercase()

                                    if (!emailRegex.matches(cleanEmail)) {
                                        Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    if (isRegistering && fullName.trim().isBlank()) {
                                        Toast.makeText(context, "Please enter your full name", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }

                                    isSendingEmail = true
                                    Toast.makeText(context, "Checking account status...", Toast.LENGTH_SHORT).show()

                                    scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                        try {
                                            // Check database for profile existence
                                            val profile = try {
                                                Supabase.client.postgrest["profiles"]
                                                    .select { filter { eq("email", cleanEmail) } }
                                                    .decodeSingleOrNull<DbProfile>()
                                            } catch (e: Exception) {
                                                null
                                            }

                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                if (isRegistering) {
                                                    // User is trying to register
                                                    if (profile != null) {
                                                        // Account already exists! Switch them to login
                                                        isSendingEmail = false
                                                        Toast.makeText(context, "Email is already registered. Switching to Sign In.", Toast.LENGTH_LONG).show()
                                                        isRegistering = false
                                                    } else {
                                                        // New account! Send OTP
                                                        sendOtp(cleanEmail)
                                                    }
                                                } else {
                                                    // User is trying to login/sign in
                                                    if (profile != null) {
                                                        // Account exists! Send OTP
                                                        sendOtp(cleanEmail)
                                                    } else {
                                                        // Account not found! Show register dialog
                                                        isSendingEmail = false
                                                        showRegisterDialog = true
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                isSendingEmail = false
                                                Toast.makeText(context, "Error verifying account: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isSendingEmail,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (isSendingEmail) {
                                    CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = if (isRegistering) "Register & Send OTP" else "Continue",
                                        color = White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Switch mode link (Register vs Sign In)
                            Text(
                                text = if (isRegistering) "Already have an account? Sign In" else "New to Reward Club? Register Now",
                                color = DarkGreen,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    isRegistering = !isRegistering
                                    otpCode = ""
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Continue as Guest",
                                color = TextGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.clickable {
                                    com.rewardclub.app.utils.UserSession.isGuest = true
                                    onLoginSuccess()
                                }
                            )
                        }

                        LoginStep.ENTER_OTP -> {
                            Text(
                                text = "Verify OTP",
                                color = TextDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Code sent to $emailAddress",
                                    color = TextGray,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Edit",
                                    color = DarkGreen,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable {
                                        currentStep = LoginStep.ENTER_EMAIL
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(28.dp))

                            // OTP Inputs
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
                                                    .background(Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp))
                                                    .border(
                                                        width = if (isFocused) 2.dp else 1.dp,
                                                        color = if (isFocused) DarkGreen else BorderColor,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .shadow(if (isFocused) 4.dp else 0.dp, RoundedCornerShape(10.dp)),
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

                            Spacer(modifier = Modifier.height(28.dp))

                            Button(
                                onClick = {
                                    if (otpCode.length < 6) {
                                        Toast.makeText(context, "Please enter all 6 digits", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
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
                                                // Handle profile setup/update
                                                if (isRegistering) {
                                                    // Update full name in database
                                                    try {
                                                        Supabase.client.postgrest["profiles"].update(
                                                            {
                                                                set("full_name", fullName.trim())
                                                            }
                                                        ) {
                                                            filter { eq("id", user.id) }
                                                        }
                                                    } catch (e: Exception) {
                                                        // Non-blocking update failure catch
                                                    }

                                                    scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                        isSendingEmail = false
                                                        com.rewardclub.app.utils.UserSession.login(
                                                            userEmail = user.email ?: emailAddress.trim(),
                                                            uid = user.id,
                                                            name = fullName.trim()
                                                        )
                                                        Toast.makeText(context, "Welcome to Reward Club!", Toast.LENGTH_SHORT).show()
                                                        onLoginSuccess()
                                                    }
                                                } else {
                                                    // Fetch user profile stats
                                                    val dbProfile = try {
                                                        Supabase.client.postgrest["profiles"]
                                                            .select { filter { eq("id", user.id) } }
                                                            .decodeSingleOrNull<DbProfile>()
                                                    } catch (e: Exception) {
                                                        null
                                                    }

                                                    scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                        isSendingEmail = false
                                                        com.rewardclub.app.utils.UserSession.login(
                                                            userEmail = user.email ?: emailAddress.trim(),
                                                            uid = user.id,
                                                            name = dbProfile?.full_name ?: ""
                                                        )
                                                        Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                                        onLoginSuccess()
                                                    }
                                                }
                                            } else {
                                                scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                    isSendingEmail = false
                                                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
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
                                shape = RoundedCornerShape(10.dp),
                                enabled = !isSendingEmail,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                if (isSendingEmail) {
                                    CircularProgressIndicator(color = White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                } else {
                                    Text("Verify & Complete", fontWeight = FontWeight.Bold, color = White, fontSize = 15.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Resend and Back Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Back",
                                    color = TextGray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable {
                                        currentStep = LoginStep.ENTER_EMAIL
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
                                        text = "Resend OTP",
                                        fontSize = 13.sp,
                                        color = DarkGreen,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable {
                                            otpCode = ""
                                            scope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                                try {
                                                    Supabase.client.auth.signInWith(OTP) {
                                                        email = emailAddress.trim()
                                                    }
                                                    scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                        countdownTime = 30
                                                        Toast.makeText(context, "OTP Code Resent!", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (e: Exception) {
                                                    scope.launch(kotlinx.coroutines.Dispatchers.Main) {
                                                        Toast.makeText(context, "Resend failed: ${e.message}", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
