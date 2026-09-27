// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.UserSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    onBackClick: () -> Unit = {},
    onContactUsClick: () -> Unit = {},
    onTransactionHistoryClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val userSession = UserSession

    // Profile state - Check real session
    val isGuest = userSession.currentUser == null || userSession.isGuest
    var fullName by remember(userSession.fullName, isGuest) {
        mutableStateOf(
            if (!isGuest && userSession.fullName.isNotEmpty()) userSession.fullName
            else if (!isGuest && userSession.email.isNotEmpty()) userSession.email.substringBefore("@").replaceFirstChar { it.uppercase() }
            else "Guest User"
        )
    }
    var email by remember(userSession.email, isGuest) {
        mutableStateOf(
            if (!isGuest && userSession.email.isNotEmpty()) userSession.email
            else "Not Logged In"
        )
    }
    var mobile by remember(userSession.mobile, isGuest) {
        mutableStateOf(
            if (!isGuest && userSession.mobile.isNotEmpty()) userSession.mobile
            else "Not Logged In"
        )
    }
    val userId = remember(userSession.currentUser, isGuest) {
        val uid = userSession.currentUser?.uid ?: ""
        if (!isGuest && uid.isNotEmpty()) "RC${uid.take(5).uppercase()}" else "GUEST"
    }

    // Available & Pending Coins (Strict 0 Coins for Guests)
    val availableCoins = remember(userSession.totalCoins, isGuest) {
        if (!isGuest) userSession.totalCoins else 0L
    }
    val pendingCoins = remember(userSession.pendingCoins, isGuest) {
        if (!isGuest) userSession.pendingCoins else 0L
    }

    // Modal / Dialog States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showWithdrawalDialog by remember { mutableStateOf(false) }
    var showReferEarnDialog by remember { mutableStateOf(false) }

    // Withdrawal Form State
    var upiIdInput by remember { mutableStateOf("") }
    var withdrawCoinsInput by remember { mutableStateOf("") }
    var isSubmittingWithdrawal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Bar Header with Creamy Gradient (Matching Splash Theme) ───────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFDF8), // Luxury Ivory Cream
                            Color(0xFFFAF4E8), // Soft Warm Cream
                            Color(0xFFF5EAD4)  // Golden Ivory Tint
                        )
                    )
                )
                .border(BorderStroke(1.dp, Color(0xFFF1E6D0)), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .padding(top = 50.dp, bottom = 32.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Profile",
                        color = Color(0xFF0F172A),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Manage your account and earnings",
                        color = Color(0xFF475569),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Notification Bell Icon with Red Dot Badge
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFF1E6D0), CircleShape)
                        .clickable {
                            Toast.makeText(context, "🔔 No new notifications", Toast.LENGTH_SHORT).show()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = Color(0xFF0F172A),
                        modifier = Modifier.size(22.dp)
                    )
                    // Red Notification Dot Badge
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(Color(0xFFEF4444), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape)
                            .align(Alignment.TopEnd)
                            .offset(x = (-6).dp, y = 6.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── User Profile Card (Hero Card) ──────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Row: Avatar Ring + User Details (100% Full Width, ZERO Collisions!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Profile Avatar Ring with Camera Badge
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .shadow(4.dp, CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF6366F1), Color(0xFF3B82F6))
                                        ),
                                        CircleShape
                                    )
                                    .padding(2.5.dp)
                                    .background(Color(0xFF1E293B), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = fullName.split(" ").mapNotNull { it.firstOrNull() }.take(2)
                                        .joinToString("").uppercase().ifEmpty { "GU" },
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            // Camera Icon Badge at Bottom Right
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .background(Color(0xFF6366F1), CircleShape)
                                    .border(2.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd)
                                    .clickable {
                                        showEditProfileDialog = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Edit photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        // User Info Details (Full width, zero truncation!)
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = fullName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            // User ID Row with Copy Icon
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.clickable {
                                    clipboardManager.setText(AnnotatedString(userId))
                                    Toast.makeText(context, "User ID copied: $userId", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(
                                    text = "User ID: $userId",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy User ID",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(13.dp)
                                )
                            }

                            // Email Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = email,
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)

                    // Bottom Row: Edit Profile Button (Sleek full-width button, zero overlap!)
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF2563EB)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Edit Profile & Account Details",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Coins Overview Grid Cards (2 Compact Cards) ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Card 1: Available Coins (Soft Green)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFA7F3D0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪙", fontSize = 16.sp)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            Text(
                                text = "Available Coins",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "%,d Coins".format(availableCoins),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                            Text(
                                text = "4 Coins = ₹1",
                                fontSize = 9.5.sp,
                                color = Color(0xFF166534)
                            )
                        }
                    }
                }

                // Card 2: Pending Coins (Soft Orange)
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEDD5)),
                    border = BorderStroke(1.dp, Color(0xFFFED7AA))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFFDBA74).copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪙", fontSize = 16.sp)
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                            Text(
                                text = "Pending Coins",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF9A3412)
                            )
                            Text(
                                text = "%,d Coins".format(pendingCoins),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C)
                            )
                            Text(
                                text = "4 Coins = ₹1",
                                fontSize = 9.5.sp,
                                color = Color(0xFF9A3412)
                            )
                        }
                    }
                }
            }

            // ── Menu List Options (4 Cards) ──────────────────────────────────

            // Option 1: History
            ProfileOptionCard(
                icon = Icons.Default.FormatListBulleted,
                iconBg = Color(0xFFF3E8FF),
                iconTint = Color(0xFF7C3AED),
                title = "History",
                subtitle = "View your earnings, withdrawals and more",
                onClick = onTransactionHistoryClick
            )

            // Option 2: Withdrawal Request
            ProfileOptionCard(
                icon = Icons.Default.AccountBalance,
                iconBg = Color(0xFFFFE4E6),
                iconTint = Color(0xFFE11D48),
                title = "Withdrawal Request",
                subtitle = "Request to withdraw your earnings",
                onClick = { showWithdrawalDialog = true }
            )

            // Option 3: Refer & Earn
            ProfileOptionCard(
                icon = Icons.Default.GroupAdd,
                iconBg = Color(0xFFDCFCE7),
                iconTint = Color(0xFF16A34A),
                title = "Refer & Earn",
                subtitle = "Invite friends and earn more coins",
                onClick = { showReferEarnDialog = true }
            )

            // Option 4: Contact Us
            ProfileOptionCard(
                icon = Icons.Default.HeadsetMic,
                iconBg = Color(0xFFE0F2FE),
                iconTint = Color(0xFF0284C7),
                title = "Contact Us",
                subtitle = "Get help and support",
                onClick = onContactUsClick
            )

            // ── Referral / Promo Card Banner ──────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReferEarnDialog = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
                border = BorderStroke(1.dp, Color(0xFFE9D5FF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFDDD6FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎁", fontSize = 24.sp)
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Invite Friends & Earn More Coins",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5B21B6)
                        )
                        Text(
                            text = "Share your referral link and earn unlimited coins",
                            fontSize = 12.sp,
                            color = Color(0xFF7C3AED)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // ── Sign Out Button ──────────────────────────────────────────────
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = {
                    userSession.logout()
                    Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, Color(0xFFEF4444)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ── Dialog: Edit Profile ────────────────────────────────────────────────
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB))
                    Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2563EB)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF2563EB)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF2563EB)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        userSession.updateProfile(fullName, mobile) { _ -> }
                        showEditProfileDialog = false
                        Toast.makeText(context, "✓ Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // ── Dialog: Instant UPI Withdrawal Request ─────────────────────────────
    if (showWithdrawalDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawalDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFFE11D48))
                    Text("UPI Withdrawal Request", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Enter your UPI ID to transfer your coins directly into your bank account. Rate: 4 Coins = ₹1.",
                        fontSize = 12.5.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = { upiIdInput = it },
                        label = { Text("Enter UPI ID (e.g. mobile@upi)", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFFE11D48)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = withdrawCoinsInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) withdrawCoinsInput = it },
                        label = { Text("Coins to Withdraw (Min 100)", fontSize = 12.sp) },
                        leadingIcon = { Text("🪙", fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val num = withdrawCoinsInput.toLongOrNull() ?: 0L
                        if (!upiIdInput.contains("@")) {
                            Toast.makeText(context, "Please enter a valid UPI ID", Toast.LENGTH_SHORT).show()
                        } else if (num < 100L) {
                            Toast.makeText(context, "Minimum withdrawal is 100 coins (₹25)", Toast.LENGTH_SHORT).show()
                        } else {
                            isSubmittingWithdrawal = true
                            scope.launch {
                                userSession.requestUpiWithdrawal(upiIdInput.trim(), num)
                                isSubmittingWithdrawal = false
                                showWithdrawalDialog = false
                                Toast.makeText(
                                    context,
                                    "✓ Request submitted! ₹${num / 4.0} will be deposited to $upiIdInput",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isSubmittingWithdrawal) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    } else {
                        Text("Request Transfer", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawalDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }

    // ── Dialog: Refer & Earn ────────────────────────────────────────────────
    if (showReferEarnDialog) {
        AlertDialog(
            onDismissRequest = { showReferEarnDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🎁", fontSize = 20.sp)
                    Text("Refer & Earn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Share your unique referral code with friends. Earn 500 bonus coins when they complete their first order!",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF3E8FF),
                        border = BorderStroke(1.dp, Color(0xFFDDD6FE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REWARD2026",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6D28D9)
                            )

                            Button(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString("REWARD2026"))
                                    Toast.makeText(context, "Referral Code REWARD2026 copied!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D28D9)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Copy Code", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Join Reward Club using my referral code REWARD2026 and get bonus coins! https://rewardclub.app")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Referral Link"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Share Link", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReferEarnDialog = false }) {
                    Text("Close", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ProfileOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Soft Colored Square Icon Box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(iconBg, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Title & Subtitle Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            // Right Chevron Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
