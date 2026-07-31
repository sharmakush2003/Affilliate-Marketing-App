// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutCompanyScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()

    // Pulse animation for logo
    val pulse = rememberInfiniteTransition(label = "pulse")
    val scale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val milestones = listOf(
        Triple("2022", "Founded", "Reward Club was born with a mission to reward every Indian shopper."),
        Triple("2023", "10K Users", "Crossed 10,000 active members earning coins daily."),
        Triple("2024", "50+ Brands", "Partnered with 50+ top brands including Amazon & Flipkart."),
        Triple("2025", "₹1Cr+ Rewards", "Over ₹1 Crore worth of rewards distributed to members.")
    )

    val features = listOf(
        Pair("🛍️", "Earn on Every Purchase"),
        Pair("💰", "Zero Hidden Charges"),
        Pair("🔒", "Bank-Grade Security"),
        Pair("⚡", "Instant Coin Credit"),
        Pair("🎁", "Exclusive Member Deals"),
        Pair("📱", "24/7 App Support")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkGreen)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4F6F8))
                .padding(padding)
                .verticalScroll(scrollState)
        ) {

            // ── Hero Section ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DarkGreen, Color(0xFF1B5E20))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Background decorative circles
                Box(
                    modifier = Modifier
                        .size(340.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = 80.dp, y = (-40).dp)
                        .background(Color.White.copy(alpha = 0.04f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .align(Alignment.BottomStart)
                        .offset(x = (-60).dp, y = 60.dp)
                        .background(Color.White.copy(alpha = 0.06f), CircleShape)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pulsing Logo
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .graphicsLayer(scaleX = scale, scaleY = scale)
                            .shadow(12.dp, CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                                ),
                                shape = CircleShape
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.reward_club_logo),
                            contentDescription = "Reward Club Logo",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Reward Club",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "India's #1 Affiliate Rewards Platform",
                            color = AccentGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Tag chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Since 2022", "Trusted by 50K+", "Top Rated").forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(text = tag, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Mission Statement ─────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎯", fontSize = 20.sp)
                        }
                        Text(
                            text = "Our Mission",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Text(
                        text = "We believe every purchase you make should reward you back. Reward Club turns your everyday online shopping into a coins-earning journey — shop on Amazon, Flipkart, Myntra and more, and watch your coins grow with every click.",
                        fontSize = 14.sp,
                        color = Color(0xFF555555),
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Stats Row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    Triple("50K+", "Members", "👥"),
                    Triple("50+", "Brands", "🏪"),
                    Triple("₹1Cr+", "Rewarded", "💰")
                ).forEach { (value, label, icon) ->
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(icon, fontSize = 22.sp)
                            Text(
                                text = value,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DarkGreen
                            )
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Why Choose Us ─────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFFFF8E1), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⭐", fontSize = 20.sp)
                        }
                        Text(
                            text = "Why Choose Us",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // 2-column grid
                    features.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { (emoji, label) ->
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFE8F5E9), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(emoji, fontSize = 17.sp)
                                    }
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF333333),
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Journey / Timeline ────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🚀", fontSize = 20.sp)
                        }
                        Text(
                            text = "Our Journey",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    milestones.forEachIndexed { index, (year, title, desc) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Timeline dot + line
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            brush = Brush.linearGradient(
                                                listOf(DarkGreen, Color(0xFF66BB6A))
                                            ),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (index < milestones.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(50.dp)
                                            .background(Color(0xFFDDDDDD))
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(DarkGreen.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(year, fontSize = 11.sp, color = DarkGreen, fontWeight = FontWeight.Bold)
                                    }
                                    Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                                }
                                Text(
                                    desc,
                                    fontSize = 12.sp,
                                    color = Color(0xFF777777),
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(bottom = if (index < milestones.lastIndex) 16.dp else 0.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Contact / Footer ──────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📬", fontSize = 28.sp)
                    Text(
                        text = "Get in Touch",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📧 Email", fontSize = 13.sp, color = Color.Gray)
                        Text("rewardclub.team@gmail.com", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌐 Website", fontSize = 13.sp, color = Color.Gray)
                        Text("www.rewardclub.in", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkGreen)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📍 Based in", fontSize = 13.sp, color = Color.Gray)
                        Text("India 🇮🇳", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Version footer
            Text(
                text = "Reward Club  •  v3.2.1  •  Made with ❤️ in India",
                fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 28.dp)
            )
        }
    }
}
