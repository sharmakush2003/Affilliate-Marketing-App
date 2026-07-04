package com.example.xyz.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xyz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    // Form state
    var fullName by remember { mutableStateOf("Kush Sharma") }
    var email by remember { mutableStateOf("kush@example.com") }
    var mobile by remember { mutableStateOf("+91 98765 43210") }
    var receiveEmails by remember { mutableStateOf(true) }

    // Accordion state
    var settingsExpanded by remember { mutableStateOf(false) }
    var deleteExpanded by remember { mutableStateOf(false) }
    var deleteDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDeleteReason by remember { mutableStateOf("") }
    var otherReason by remember { mutableStateOf("") }

    val deleteReasons = listOf(
        "I no longer use the app",
        "I have a duplicate account",
        "I have privacy concerns",
        "The app is not useful for me",
        "I am not satisfied with the rewards",
        "Others"
    )

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

        // ── Stats Row ─────────────────────────────────────────────────────────
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
            StatItem(value = "2,450", label = "Total\nCoins", emoji = "🪙", valueColor = DarkGreen)
            VerticalDividerLine()
            StatItem(value = "800", label = "Redeemed\nCoins", emoji = "🎁", valueColor = Color(0xFFF57C00))
            VerticalDividerLine()
            StatItem(value = "₹612", label = "Total\nSavings", emoji = "💰", valueColor = Color(0xFF1565C0))
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                    onClick = { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.Redeem,
                    iconBg = Color(0xFFFFF8E1),
                    iconTint = Color(0xFFF57C00),
                    title = "Redemption History",
                    subtitle = "See all redeemed coins",
                    onClick = { Toast.makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.Share,
                    iconBg = Color(0xFFE8F5E9),
                    iconTint = DarkGreen,
                    title = "Refer & Earn",
                    subtitle = "Invite friends and earn bonus coins",
                    onClick = { Toast.makeText(context, "Referral code: RC2024", Toast.LENGTH_SHORT).show() }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF5F5F5))
                QuickMenuRow(
                    icon = Icons.Default.SupportAgent,
                    iconBg = Color(0xFFF3E5F5),
                    iconTint = Color(0xFF7B1FA2),
                    title = "Contact Us",
                    subtitle = "Email · Call · Chat support",
                    onClick = {
                        Toast.makeText(context, "📧 rewardclub.team@gmail.com\n📞 1800-XXX-XXXX", Toast.LENGTH_LONG).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Delete Account Accordion ──────────────────────────────────────────
        ProfileSectionCard(
            icon = Icons.Default.DeleteForever,
            iconBg = Color(0xFFFFEBEE),
            iconTint = Color(0xFFD32F2F),
            title = "Request Account Deletion",
            subtitle = if (deleteExpanded) "Tap to close" else "Permanently remove your account & data",
            expanded = deleteExpanded,
            onToggle = { deleteExpanded = !deleteExpanded },
            headerTextColor = Color(0xFFD32F2F)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "This is permanent. All your coins, order history and data will be deleted and cannot be recovered.",
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    lineHeight = 18.sp
                )

                ExposedDropdownMenuBox(
                    expanded = deleteDropdownExpanded,
                    onExpandedChange = { deleteDropdownExpanded = !deleteDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedDeleteReason.ifEmpty { "Select a reason..." },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Reason for deletion") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deleteDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F),
                            focusedLabelColor = Color(0xFFD32F2F),
                            unfocusedTextColor = if (selectedDeleteReason.isEmpty()) Color.Gray else Color(0xFF1A1A1A)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = deleteDropdownExpanded,
                        onDismissRequest = { deleteDropdownExpanded = false }
                    ) {
                        deleteReasons.forEach { reason ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        reason, fontSize = 14.sp,
                                        color = if (reason == selectedDeleteReason) Color(0xFFD32F2F) else Color(0xFF1A1A1A),
                                        fontWeight = if (reason == selectedDeleteReason) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                onClick = { selectedDeleteReason = reason; deleteDropdownExpanded = false }
                            )
                        }
                    }
                }

                if (selectedDeleteReason == "Others") {
                    OutlinedTextField(
                        value = otherReason,
                        onValueChange = { otherReason = it },
                        label = { Text("Describe your reason") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3, maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD32F2F),
                            focusedLabelColor = Color(0xFFD32F2F),
                            cursorColor = Color(0xFFD32F2F)
                        )
                    )
                }

                if (selectedDeleteReason.isNotEmpty()) {
                    OutlinedButton(
                        onClick = { Toast.makeText(context, "Deletion request submitted. Our team will contact you within 48 hours.", Toast.LENGTH_LONG).show() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Deletion Request", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Save Changes ──────────────────────────────────────────────────────
        Button(
            onClick = { Toast.makeText(context, "✓ Changes saved successfully!", Toast.LENGTH_SHORT).show() },
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
