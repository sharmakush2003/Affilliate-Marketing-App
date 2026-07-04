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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xyz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Form state
    var fullName by remember { mutableStateOf("Kush Sharma") }
    var email by remember { mutableStateOf("kush@example.com") }
    var mobile by remember { mutableStateOf("+91 98765 43210") }
    var receiveEmails by remember { mutableStateOf(true) }

    // Delete account dropdown
    var deleteExpanded by remember { mutableStateOf(false) }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F8))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Avatar + Name Header ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(DarkGreen, Color(0xFF2E7D32)))
                    )
                    .padding(top = 24.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .padding(3.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fullName.take(2).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkGreen
                        )
                    }
                    Text(fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(mobile, fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            // ── Stats: Total Points + Redeemed ───────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Points card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFE8F5E9), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🪙", fontSize = 22.sp)
                        }
                        Text(
                            text = "2,450",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = DarkGreen
                        )
                        Text(
                            text = "Total Points",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Total Redeemed card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFFFF8E1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎁", fontSize = 22.sp)
                        }
                        Text(
                            text = "800",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFF57C00)
                        )
                        Text(
                            text = "Redeemed Coins",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── Account Settings ─────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = DarkGreen, modifier = Modifier.size(20.dp))
                        Text("Account Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                    }

                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DarkGreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            focusedLabelColor = DarkGreen,
                            cursorColor = DarkGreen
                        ),
                        singleLine = true
                    )

                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = DarkGreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            focusedLabelColor = DarkGreen,
                            cursorColor = DarkGreen
                        ),
                        singleLine = true
                    )

                    // Mobile Number
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = DarkGreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DarkGreen,
                            focusedLabelColor = DarkGreen,
                            cursorColor = DarkGreen
                        ),
                        singleLine = true
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    // Receive Emails toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF555555), modifier = Modifier.size(20.dp))
                            Column {
                                Text("Receive Emails", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                                Text("Get offers, coin alerts & updates", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = receiveEmails,
                            onCheckedChange = { receiveEmails = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DarkGreen,
                                uncheckedThumbColor = Color.Gray,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }
                }
            }

            // ── Request Delete Account ────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                        Text("Request Account Deletion", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    }

                    Text(
                        text = "Please let us know why you want to delete your account. This action is permanent and all your coins will be forfeited.",
                        fontSize = 12.sp,
                        color = Color(0xFF777777),
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = Color(0xFFF0F0F0))

                    // Dropdown for reason
                    ExposedDropdownMenuBox(
                        expanded = deleteExpanded,
                        onExpandedChange = { deleteExpanded = !deleteExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDeleteReason.ifEmpty { "Select a reason..." },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Reason for deletion") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = deleteExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD32F2F),
                                focusedLabelColor = Color(0xFFD32F2F),
                                unfocusedTextColor = if (selectedDeleteReason.isEmpty()) Color.Gray else Color(0xFF1A1A1A)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = deleteExpanded,
                            onDismissRequest = { deleteExpanded = false }
                        ) {
                            deleteReasons.forEach { reason ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            reason,
                                            fontSize = 14.sp,
                                            color = if (reason == selectedDeleteReason) DarkGreen else Color(0xFF1A1A1A),
                                            fontWeight = if (reason == selectedDeleteReason) FontWeight.SemiBold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedDeleteReason = reason
                                        deleteExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Show text field if "Others" selected
                    if (selectedDeleteReason == "Others") {
                        OutlinedTextField(
                            value = otherReason,
                            onValueChange = { otherReason = it },
                            label = { Text("Please specify your reason") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            minLines = 3,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD32F2F),
                                focusedLabelColor = Color(0xFFD32F2F),
                                cursorColor = Color(0xFFD32F2F)
                            )
                        )
                    }

                    // Delete Account button (only active when reason selected)
                    if (selectedDeleteReason.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "Delete request submitted. Our team will contact you.", Toast.LENGTH_LONG).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submit Deletion Request", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── Save Changes Button ───────────────────────────────────────────
            Button(
                onClick = {
                    Toast.makeText(context, "Changes saved successfully! ✓", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
