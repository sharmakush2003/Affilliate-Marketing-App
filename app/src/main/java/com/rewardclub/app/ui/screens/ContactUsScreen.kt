// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.UserSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsScreen(
    onBackClick: () -> Unit,
    onNavigateToFaqs: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userSession = UserSession

    // Coming Soon Dialog State
    var showComingSoonDialog by remember { mutableStateOf(false) }
    var comingSoonTitle by remember { mutableStateOf("") }
    var comingSoonMessage by remember { mutableStateOf("") }
    var comingSoonEmoji by remember { mutableStateOf("🚀") }

    fun openComingSoon(title: String, message: String, emoji: String = "🚀") {
        comingSoonTitle = title
        comingSoonMessage = message
        comingSoonEmoji = emoji
        showComingSoonDialog = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top Gradient Header Bar (Spacious & Premium) ───────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4F46E5), // Indigo / Violet
                            Color(0xFF2563EB)  // Blue
                        )
                    )
                )
                .padding(top = 52.dp, bottom = 36.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Contact Us",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "We're here to help you",
                        color = Color(0xFFE0E7FF),
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                // Balance spacer for true centering
                Spacer(modifier = Modifier.size(40.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Hero Banner Card ("Need Help?") ─────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                border = BorderStroke(1.dp, Color(0xFFE0E7FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Column Text & Highlights
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Need Help?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0F172A)
                        )

                        Text(
                            text = "Our support team is always ready to assist you. Feel free to reach out!",
                            fontSize = 12.5.sp,
                            color = Color(0xFF475569),
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Highlight Badges
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Badge 1: Fast Support
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(Color(0xFF2563EB), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Text(
                                    text = "Fast Support",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            // Badge 2: Response Within 24 Hours
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Response Within 24 Hours",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                            }

                            // Badge 3: Dedicated Support Team
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "Dedicated Support Team",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right Side: 3D Illustration Avatar with Speech Bubble
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Speech Bubble "We're Here for You!"
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f)),
                            shadowElevation = 2.dp
                        ) {
                            Text(
                                text = "We're\nHere for You!",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1D4ED8),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Avatar Image
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_contact_support),
                                contentDescription = "Support Agent",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // ── Option Card 1: Email Us ───────────────────────────────────────
            ContactOptionCard(
                icon = Icons.Default.Email,
                iconBg = Color(0xFFE0F2FE),
                iconTint = Color(0xFF0284C7),
                title = "Email Us",
                subtitle = "Send us an email for any queries or support.",
                linkText = "rewardclub.team@gmail.com",
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:rewardclub.team@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Reward Club App Inquiry")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Email: rewardclub.team@gmail.com", Toast.LENGTH_LONG).show()
                    }
                }
            )

            // ── Option Card 2: Request Call Back ────────────────────────────
            ContactOptionCard(
                icon = Icons.Default.Call,
                iconBg = Color(0xFFDCFCE7),
                iconTint = Color(0xFF16A34A),
                title = "Request Call Back",
                subtitle = "Submit your details and we'll call you back.\nWe're available from 10 AM to 6 PM (Mon - Sat)",
                onClick = {
                    openComingSoon(
                        title = "Request Call Back",
                        message = "Our direct callback feature is launching soon! In the meantime, please send us an email at rewardclub.team@gmail.com and we will respond within 24 hours.",
                        emoji = "📞"
                    )
                }
            )

            // ── Option Card 3: FAQs ──────────────────────────────────────────
            ContactOptionCard(
                icon = Icons.Default.Info,
                iconBg = Color(0xFFFEF9C3),
                iconTint = Color(0xFFCA8A04),
                title = "FAQs",
                subtitle = "Find answers to commonly asked questions.",
                onClick = {
                    openComingSoon(
                        title = "Frequently Asked Questions",
                        message = "Our comprehensive FAQ section is currently being updated with new guides and wallet help articles. Coming soon!",
                        emoji = "❓"
                    )
                }
            )

            // ── Option Card 4: Rate Us ───────────────────────────────────────
            ContactOptionCard(
                icon = Icons.Default.Star,
                iconBg = Color(0xFFFFE4E6),
                iconTint = Color(0xFFE11D48),
                title = "Rate Us",
                subtitle = "Your feedback helps us improve.",
                onClick = {
                    openComingSoon(
                        title = "Play Store Rating",
                        message = "Play Store rating integration is coming soon upon our official store release! We appreciate your support.",
                        emoji = "⭐"
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Thank You Banner ─────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                            .size(44.dp)
                            .background(Color(0xFFDDD6FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF7C3AED),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Thank you for being a part of our journey!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6D28D9)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Your support means a lot to us.",
                            fontSize = 12.sp,
                            color = Color(0xFF7E22CE)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ── Dialog: Feature Coming Soon ──────────────────────────────────────────
    if (showComingSoonDialog) {
        AlertDialog(
            onDismissRequest = { showComingSoonDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0xFFEEF2FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(comingSoonEmoji, fontSize = 20.sp)
                    }
                    Text(
                        text = comingSoonTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF0F172A)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = comingSoonMessage,
                        fontSize = 13.5.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("✉️", fontSize = 16.sp)
                            Text(
                                text = "Need help? Email us: rewardclub.team@gmail.com",
                                fontSize = 11.5.sp,
                                color = Color(0xFF2563EB),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showComingSoonDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Got It!", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            shape = RoundedCornerShape(22.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun ContactOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    linkText: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Icon Square Box
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

            // Text info
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
                    color = Color(0xFF64748B),
                    lineHeight = 16.sp
                )

                if (!linkText.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = linkText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconTint
                    )
                }
            }

            // Chevron Right Arrow
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
