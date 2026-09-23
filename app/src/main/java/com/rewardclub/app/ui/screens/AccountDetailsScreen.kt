// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import io.github.jan.supabase.postgrest.postgrest
import com.rewardclub.app.utils.Supabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isSendingFeedback by remember { mutableStateOf(false) }

    val userSession = com.rewardclub.app.utils.UserSession
    // Form state
    val isGuest = userSession.isGuest
    var fullName by remember { mutableStateOf(if (isGuest && userSession.fullName.isEmpty()) "Guest User" else userSession.fullName) }
    var email by remember { mutableStateOf(if (isGuest && userSession.email.isEmpty()) "guest@rewardclub.com" else userSession.email) }
    var mobile by remember { mutableStateOf(if (isGuest && userSession.mobile.isEmpty()) "Not Linked" else userSession.mobile) }
    var receiveEmails by remember { mutableStateOf(true) }

    LaunchedEffect(userSession.fullName, userSession.email, userSession.mobile, isGuest) {
        fullName = if (isGuest && userSession.fullName.isEmpty()) "Guest User" else userSession.fullName
        email = if (isGuest && userSession.email.isEmpty()) "guest@rewardclub.com" else userSession.email
        mobile = if (isGuest && userSession.mobile.isEmpty()) "Not Linked" else userSession.mobile
    }

    // Accordion state
    var settingsExpanded by remember { mutableStateOf(false) }

    // Feedback dialog state
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var feedbackType by remember { mutableStateOf("General Feedback") }
    var feedbackMessage by remember { mutableStateOf("") }
    var feedbackRating by remember { mutableStateOf(5) }

    // Activity dialog states
    var showOrderHistory by remember { mutableStateOf(false) }
    var showRedemptionHistory by remember { mutableStateOf(false) }
    var showReferEarn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA))
            .verticalScroll(rememberScrollState())
    ) {

        // ── Hero Header ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp, bottom = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar ring
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .shadow(8.dp, CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                            ),
                            CircleShape
                        )
                        .padding(3.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fullName.split(" ").mapNotNull { it.firstOrNull() }.take(2)
                            .joinToString("").uppercase(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkGreen
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = fullName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = email,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = mobile,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile chip
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(50))
                        .clickable { settingsExpanded = true }
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text("Edit Profile", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // ── Stats Row (Total Coins, Pending Coins, Withdrawn Coins) ─────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-20).dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = userSession.totalCoins.toString(), label = "Total\nCoins", emoji = "🪙", valueColor = DarkGreen)
            VerticalDividerLine()
            StatItem(value = userSession.pendingCoins.toString(), label = "Pending\nCoins", emoji = "⏳", valueColor = Color(0xFFD97706))
            VerticalDividerLine()
            StatItem(value = userSession.withdrawnCoins.toString(), label = "Withdrawn\nCoins", emoji = "💸", valueColor = Color(0xFF2563EB))
        }

        // ── Responsive Instant UPI Withdrawal Card ───────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header: Title & Exchange Rate Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⚡", fontSize = 17.sp)
                        }
                        Column {
                            Text(
                                text = "Instant UPI Withdrawal",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Direct Bank Transfer via UPI",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEF3C7), RoundedCornerShape(8.dp))
                            .border(0.8.dp, Color(0xFFFDE68A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "4 Coins = ₹1",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309)
                        )
                    }
                }

                // Balance Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF0FDF4),
                    border = BorderStroke(1.dp, Color(0xFFDCFCE7))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Available to Withdraw", fontSize = 11.5.sp, color = Color(0xFF166534), fontWeight = FontWeight.Medium)
                        Text(
                            text = "${userSession.pendingCoins} Coins (₹${String.format("%.2f", userSession.pendingCoins / 4.0)})",
                            fontSize = 12.5.sp,
                            color = DarkGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                var upiIdInput by remember { mutableStateOf("") }
                var withdrawCoinsInput by remember { mutableStateOf("") }
                var isSubmittingWithdrawal by remember { mutableStateOf(false) }
                var showWithdrawalSuccessDialog by remember { mutableStateOf(false) }
                var lastWithdrawnAmount by remember { mutableStateOf(0.0) }
                var lastWithdrawnUpi by remember { mutableStateOf("") }

                // UPI ID Input
                OutlinedTextField(
                    value = upiIdInput,
                    onValueChange = { upiIdInput = it },
                    label = { Text("Enter UPI ID (VPA)", fontSize = 12.sp) },
                    placeholder = { Text("e.g. 9876543210@paytm or id@okhdfcbank", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = DarkGreen
                    )
                )

                // Coins to Cash Out Input
                OutlinedTextField(
                    value = withdrawCoinsInput,
                    onValueChange = { if (it.all { char -> char.isDigit() }) withdrawCoinsInput = it },
                    label = { Text("Coins to Withdraw (Min 100)", fontSize = 12.sp) },
                    placeholder = { Text("Enter coin count", fontSize = 12.sp, color = Color(0xFF94A3B8)) },
                    leadingIcon = { Text("🪙", fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp)) },
                    trailingIcon = {
                        Surface(
                            onClick = {
                                if (userSession.pendingCoins > 0) {
                                    withdrawCoinsInput = userSession.pendingCoins.toString()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                "MAX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = DarkGreen,
                        unfocusedBorderColor = Color(0xFFCBD5E1),
                        focusedLabelColor = DarkGreen
                    )
                )

                // Scrollable Preset Chips (Never overflows or clips)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100L to "₹25", 250L to "₹62.50", 500L to "₹125", 1000L to "₹250", 2000L to "₹500").forEach { (presetCoins, inr) ->
                        val isSelected = withdrawCoinsInput == presetCoins.toString()
                        Surface(
                            onClick = { withdrawCoinsInput = presetCoins.toString() },
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) DarkGreen else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isSelected) DarkGreen else Color(0xFFE2E8F0))
                        ) {
                            Text(
                                text = "$presetCoins 🪙 ($inr)",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextDark,
                                modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        val trimmedUpi = upiIdInput.trim()
                        val coinsNum = withdrawCoinsInput.toLongOrNull() ?: 0L
                        if (!trimmedUpi.contains("@")) {
                            Toast.makeText(context, "Please enter a valid UPI ID (e.g. mobile@upi)", Toast.LENGTH_SHORT).show()
                        } else if (coinsNum < 100L) {
                            Toast.makeText(context, "Minimum withdrawal is 100 Coins (₹25)", Toast.LENGTH_SHORT).show()
                        } else if (coinsNum > userSession.pendingCoins) {
                            Toast.makeText(context, "Insufficient coins! You have ${userSession.pendingCoins} pending coins.", Toast.LENGTH_SHORT).show()
                        } else {
                            isSubmittingWithdrawal = true
                            scope.launch {
                                val ok = userSession.requestUpiWithdrawal(trimmedUpi, coinsNum)
                                isSubmittingWithdrawal = false
                                lastWithdrawnAmount = coinsNum / 4.0
                                lastWithdrawnUpi = trimmedUpi
                                showWithdrawalSuccessDialog = true
                                upiIdInput = ""
                                withdrawCoinsInput = ""
                            }
                        }
                    },
                    enabled = !isSubmittingWithdrawal,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    if (isSubmittingWithdrawal) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("⚡", fontSize = 15.sp)
                            Text("Request UPI Transfer", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White)
                        }
                    }
                }

                if (showWithdrawalSuccessDialog) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { showWithdrawalSuccessDialog = false }) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            elevation = CardDefaults.cardElevation(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(64.dp).background(Color(0xFFE8F5E9), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🎉", fontSize = 32.sp)
                                }
                                Text("Withdrawal Request Submitted!", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark, textAlign = TextAlign.Center)
                                Text(
                                    "₹$lastWithdrawnAmount will be deposited directly to your UPI ID:\n$lastWithdrawnUpi\n\nStatus: Pending Admin Approval (Typically within 2-24 hours)",
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF475569),
                                    textAlign = TextAlign.Center,
                                    lineHeight = 19.sp
                                )
                                Button(
                                    onClick = { showWithdrawalSuccessDialog = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Account Settings Accordion ────────────────────────────────────────
        ProfileSectionCard(
            icon = Icons.Default.ManageAccounts,
            iconBg = Color(0xFFE8F5E9),
            iconTint = DarkGreen,
            title = "Account Settings",
            subtitle = if (settingsExpanded) "Tap to close" else "Name · Email · Phone · Preferences",
            expanded = settingsExpanded,
            onToggle = { settingsExpanded = !settingsExpanded }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {

                // Full Name
                ProfileEditField(
                    label = "Full Name",
                    value = fullName,
                    onValueChange = { fullName = it },
                    icon = Icons.Default.Person,
                    placeholder = "Enter your full name"
                )

                // Email
                ProfileEditField(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    icon = Icons.Default.Email,
                    placeholder = "Enter email address"
                )

                // Mobile
                ProfileEditField(
                    label = "Mobile Number",
                    value = mobile,
                    onValueChange = { mobile = it },
                    icon = Icons.Default.Phone,
                    placeholder = "Enter mobile number"
                )

                HorizontalDivider(color = Color(0xFFF0F0F0))

                // Receive Emails toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FFF8), RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(18.dp))
                        }
                        Column {
                            Text("Receive Emails", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                            Text("Deals, coin alerts & updates", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Switch(
                        checked = receiveEmails,
                        onCheckedChange = { receiveEmails = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DarkGreen,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Quick Menu ────────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column {
                Text(
                    "My Activity",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
                    letterSpacing = 0.8.sp
                )
                QuickMenuRow(
                    icon = Icons.Default.ShoppingBag,
                    iconBg = Color(0xFFE3F2FD),
                    iconTint = Color(0xFF1565C0),
                    title = "Order History",
                    subtitle = "Track your coin orders",
                    onClick = { showOrderHistory = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.Redeem,
                    iconBg = Color(0xFFFFF8E1),
                    iconTint = Color(0xFFF57C00),
                    title = "Redemption History",
                    subtitle = "See all redeemed coins",
                    onClick = { showRedemptionHistory = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.Share,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = DarkGreen,
                    title = "Refer & Earn",
                    subtitle = "Invite friends and earn bonus coins",
                    onClick = { showReferEarn = true }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.SupportAgent,
                    iconBg = Color(0xFFF3E5F5),
                    iconTint = Color(0xFF7B1FA2),
                    title = "Contact Us",
                    subtitle = "Feedback & support form",
                    onClick = {
                        showFeedbackDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Save Changes ──────────────────────────────────────────────────────
        Button(
            onClick = {
                userSession.updateProfile(fullName, mobile) { success ->
                    if (success) {
                        Toast.makeText(context, "✓ Changes saved successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Failed to save changes. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkGreen
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Sign Out ─────────────────────────────────────────────────────────
        OutlinedButton(
            onClick = {
                userSession.logout()
                Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                onBackClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, Color(0xFFD32F2F)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text("Sign Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }


        if (showFeedbackDialog) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showFeedbackDialog = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Premium Header with Gradient Background
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF1B5E20), Color(0xFF388E3C))
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Contact Support",
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "We're here to help you",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                                IconButton(
                                    onClick = { showFeedbackDialog = false },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close dialog",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        // Scrollable Content to handle keyboard beautifully (Responsive)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Submit a query or report an issue. Our support team will get back to you shortly.",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )

                            // 1. Query Type Dropdown
                            var expandedType by remember { mutableStateOf(false) }
                            val types = listOf("General Feedback", "Bug Report", "Reward Queries", "Other Support")
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Query Type",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                ExposedDropdownMenuBox(
                                    expanded = expandedType,
                                    onExpandedChange = { expandedType = !expandedType }
                                ) {
                                    OutlinedTextField(
                                        value = feedbackType,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DarkGreen,
                                            unfocusedBorderColor = Color(0xFFE0E0E0),
                                            focusedContainerColor = Color(0xFFFAFAFA),
                                            unfocusedContainerColor = Color(0xFFFAFAFA)
                                        )
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedType,
                                        onDismissRequest = { expandedType = false }
                                    ) {
                                        types.forEach { type ->
                                            DropdownMenuItem(
                                                text = { Text(type, fontSize = 14.sp) },
                                                onClick = { feedbackType = type; expandedType = false }
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Rating Selector (Responsive Emojis)
                            val ratingLabels = listOf("Terrible", "Bad", "Okay", "Good", "Awesome!")
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Rate your experience",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                                        .padding(vertical = 10.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf("😠", "🙁", "😐", "🙂", "😄").forEachIndexed { index, emoji ->
                                        val ratingVal = index + 1
                                        val isSelected = feedbackRating == ratingVal
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clickable { feedbackRating = ratingVal }
                                                .padding(vertical = 4.dp, horizontal = 2.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .background(
                                                        if (isSelected) DarkGreen.copy(alpha = 0.15f) else Color.Transparent,
                                                        CircleShape
                                                    )
                                                    .border(
                                                        1.5.dp,
                                                        if (isSelected) DarkGreen else Color.Transparent,
                                                        CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(emoji, fontSize = 20.sp)
                                            }
                                        }
                                    }
                                }
                                Text(
                                    text = "Selected: ${ratingLabels[feedbackRating - 1]}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGreen,
                                    modifier = Modifier.align(Alignment.End)
                                )
                            }

                            // 3. Message Input Field
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Your Message / Query",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                OutlinedTextField(
                                    value = feedbackMessage,
                                    onValueChange = { feedbackMessage = it },
                                    placeholder = { Text("Write details of your support query here...", fontSize = 12.sp, color = Color.LightGray) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DarkGreen,
                                        unfocusedBorderColor = Color(0xFFE0E0E0),
                                        cursorColor = DarkGreen
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { showFeedbackDialog = false },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                                ) {
                                    Text("Cancel", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (feedbackMessage.isBlank()) {
                                            Toast.makeText(context, "Please write a message before submitting.", Toast.LENGTH_SHORT).show()
                                        } else {
                                            isSendingFeedback = true
                                            scope.launch {
                                                try {
                                                    val query = SupportQuery(
                                                        name = fullName,
                                                        email = email,
                                                        mobile = mobile,
                                                        user_id = userSession.currentUser?.uid ?: "Guest / Local User",
                                                        feedback_type = feedbackType,
                                                        rating = feedbackRating,
                                                        message = feedbackMessage
                                                    )
                                                    Supabase.client.postgrest["support_queries"].insert(query)
                                                    isSendingFeedback = false
                                                    Toast.makeText(context, "Support request submitted successfully!", Toast.LENGTH_LONG).show()
                                                    showFeedbackDialog = false
                                                    feedbackMessage = ""
                                                } catch (e: Exception) {
                                                    isSendingFeedback = false
                                                    Toast.makeText(context, "Failed to submit: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.5f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = !isSendingFeedback,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                                ) {
                                    if (isSendingFeedback) {
                                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text("Submit Query", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Order/Earnings History Dialog
        if (showOrderHistory) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showOrderHistory = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .heightIn(max = 500.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF1565C0), Color(0xFF1E88E5))
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Activation History", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("Track your affiliate earnings", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                                IconButton(
                                    onClick = { showOrderHistory = false },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // List content
                        val orders = listOf(
                            Triple("PolicyBazaar", "Vehicle Insurance (Up to 25% Earning)", "Pending Validation • 12 Aug 2026"),
                            Triple("Amazon India", "Electronics Deal (8% Earning)", "Approved • 10 Aug 2026"),
                            Triple("Flipkart", "Fashion Order (10% Earning)", "Approved • 05 Aug 2026")
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            orders.forEach { (brand, reward, statusInfo) ->
                                val isApproved = statusInfo.startsWith("Approved")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF9F9F9), RoundedCornerShape(14.dp))
                                        .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(14.dp))
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(brand, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(reward, fontSize = 12.sp, color = DarkGreen, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(statusInfo, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isApproved) "Approved" else "Pending",
                                            color = if (isApproved) Color(0xFF2E7D32) else Color(0xFFE65100),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Redemption History Dialog
        if (showRedemptionHistory) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showRedemptionHistory = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .heightIn(max = 500.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF57C00), Color(0xFFFF9800))
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Redemption History", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("List of claimed cash & vouchers", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                                IconButton(
                                    onClick = { showRedemptionHistory = false },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // List content
                        val redemptions = listOf(
                            Triple("Amazon Voucher", "₹500 Gift Voucher", "Success • Code: AMZN-500-RC9A"),
                            Triple("UPI Cash Out", "₹100 Transferred", "Success • ID: paytm@upi"),
                            Triple("Google Play Code", "₹250 Play Store Card", "Success • Code: GPY-250-8B92")
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            redemptions.forEach { (type, reward, detail) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF9F9F9), RoundedCornerShape(14.dp))
                                        .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(14.dp))
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(reward, fontSize = 12.sp, color = DarkGreen, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(detail, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Success",
                                            color = Color(0xFF2E7D32),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Refer & Earn Dialog
        if (showReferEarn) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showReferEarn = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color(0xFF2E7D32), Color(0xFF4CAF50))
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Refer & Earn", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text("Earn 10% of what your friends earn!", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                                }
                                IconButton(
                                    onClick = { showReferEarn = false },
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Invite friends to Reward Club and get a 10% lifetime bonus on all their affiliate and cashback earnings!",
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )

                            // Dash Border referral code box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                                    .border(1.5.dp, DarkGreen, RoundedCornerShape(12.dp))
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("YOUR REFERRAL CODE", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("RC2026", fontSize = 24.sp, color = DarkGreen, fontWeight = FontWeight.ExtraBold, letterSpacing = 2.sp)
                                }
                            }

                            // How it works timeline
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("How it works:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                listOf(
                                    "📲 Share your referral code with your friends",
                                    "🆕 Your friends register on Reward Club using your code",
                                    "💸 You earn a 10% cash bonus every time they make successful deals!"
                                ).forEach { step ->
                                    Text(step, fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            // Share button
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                            ) {
                                Text("Copy & Share Code", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

// ── Reusable Components ────────────────────────────────────────────────────

@Composable
private fun StatItem(value: String, label: String, emoji: String, valueColor: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = valueColor)
        Text(label, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

@Composable
private fun VerticalDividerLine() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(50.dp)
            .background(Color(0xFFEEEEEE))
    )
}

@Composable
private fun ProfileSectionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    headerTextColor: Color = Color(0xFF1A1A1A),
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(iconBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
                    }
                    Column {
                        Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = headerTextColor)
                        Text(subtitle, fontSize = 11.sp, color = Color.Gray, lineHeight = 15.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            if (expanded) Color(0xFFF0F0F0) else Color(0xFFF5F5F5),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (expanded) {
                HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                Box(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun ProfileEditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(20.dp))
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkGreen,
            focusedLabelColor = DarkGreen,
            cursorColor = DarkGreen,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            unfocusedLabelColor = Color.Gray
        )
    )
}

@Composable
private fun QuickMenuRow(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(20.dp))
    }
}

@Serializable
data class SupportQuery(
    val name: String,
    val email: String,
    val mobile: String,
    val user_id: String,
    val feedback_type: String,
    val rating: Int,
    val message: String
)
