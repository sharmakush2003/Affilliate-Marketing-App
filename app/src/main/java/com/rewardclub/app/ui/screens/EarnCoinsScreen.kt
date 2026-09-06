// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.rewardclub.app.R
import com.rewardclub.app.data.CampaignData
import com.rewardclub.app.data.CampaignItem
import com.rewardclub.app.ui.theme.White
import com.rewardclub.app.ui.theme.TextDark
import com.rewardclub.app.ui.theme.TextGray
import com.rewardclub.app.ui.theme.BorderColor
import com.rewardclub.app.ui.theme.DarkGreen
import com.rewardclub.app.ui.theme.LightGreen
import com.rewardclub.app.ui.theme.GrayBackground
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class RateRow(
    val category: String,
    val earnRate: String
)

data class TrackRow(
    val tracking: String,
    val confirmation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EarnCoinsScreen(
    brandName: String,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cuelinksApi = remember { com.rewardclub.app.api.CuelinksApiService() }

    val campaign = remember(brandName) { CampaignData.findCampaign(brandName) }

    val rates = if (campaign != null && campaign.rates.isNotEmpty()) {
        campaign.rates.map { RateRow(it.category, it.rate) }
    } else {
        when (brandName) {
            "Amazon" -> listOf(
                RateRow("Apparel & Fashion", "Upto 12 coins per ₹100"),
                RateRow("Beauty & Skincare", "Upto 12 coins per ₹100"),
                RateRow("Grocery", "Upto 6 coins per ₹100"),
                RateRow("Luggage & Bags", "Upto 10 coins per ₹100"),
                RateRow("Shoes & Watches", "Upto 10 coins per ₹100")
            )
            "Flipkart" -> listOf(
                RateRow("Grocery", "Upto 3 coins per ₹100"),
                RateRow("Books & General", "Upto 5 coins per ₹100"),
                RateRow("Home & Furniture", "Upto 2 coins per ₹100"),
                RateRow("Electronics", "Upto 200 coins/txn")
            )
            else -> listOf(
                RateRow("General Purchases", "Upto 10 coins per ₹100")
            )
        }
    }

    val trackings = List(rates.size) {
        TrackRow(
            campaign?.trackingWindow ?: "Within 24-48 hours",
            campaign?.approvalWindow ?: "Within 45-60 days"
        )
    }

    val rawUrl = campaign?.targetUrl ?: when (brandName) {
        "Amazon" -> "https://www.amazon.in"
        "Flipkart" -> "https://www.flipkart.com"
        "Myntra" -> "https://www.myntra.com"
        "HP Pay" -> "https://www.hppay.in"
        else -> "https://www.ajio.com"
    }

    val onActivateAndShop = {
        val currentUserId = com.rewardclub.app.utils.UserSession.currentUser?.uid ?: ""
        val trackingUrl = cuelinksApi.createAffiliateLink(rawUrl, userId = currentUserId)

        // Background ping to register click on Supabase
        coroutineScope.launch {
            cuelinksApi.fireAndForgetClick(rawUrl, userId = currentUserId, campaignName = brandName)
        }

        // Open in browser
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            android.net.Uri.parse(trackingUrl)
        )
        intent.setPackage("com.android.chrome")
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            intent.setPackage(null)
            context.startActivity(intent)
        }
    }

    val guidelines = listOf(
        "Start with an empty shopping cart before clicking the button.",
        "Complete your transaction in one continuous browsing session.",
        "Do not apply external/third-party coupons not hosted on Reward Club.",
        "Coins tracked within 24-48 hours and redeemable for UPI Cash & Gift Vouchers."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Offer Details",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = TextDark
                        )
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF059669)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = TextDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        bottomBar = {
            // Compact, high-converting sticky bottom bar
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(12.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Text(text = "🔒", fontSize = 10.sp)
                        Text(
                            text = "Coins tracking active • SubID Auto-Tagged",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF059669)
                        )
                    }

                    Button(
                        onClick = { onActivateAndShop() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ACTIVATE COINS & SHOP NOW",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = White,
                                letterSpacing = 0.5.sp
                            )
                            Icon(
                                Icons.Default.Launch,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = White
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── 1. HERO SPOTLIGHT CARD ────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Brand Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // High-res logo box with subtle shadow
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF8FAFC))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val logoUrl = campaign?.logoUrl
                            if (!logoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(logoUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = brandName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = brandName.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color(0xFF047857)
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = brandName,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = campaign?.subtitle ?: "Verified Merchant Partner",
                                fontSize = 11.5.sp,
                                color = TextGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF10B981), CircleShape)
                                )
                                Text(
                                    text = campaign?.categoryName ?: "Direct Partner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF047857)
                                )
                            }
                        }
                    }

                    // ── 2. MEGA REWARD SHOWCASE BOX (Gradient Wow Factor) ──
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF065F46),
                                        Color(0xFF047857),
                                        Color(0xFF059669)
                                    )
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("🔥", fontSize = 10.sp)
                                    Text(
                                        text = "MAX REWARDS ACTIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = White
                                    )
                                }

                                Text(
                                    text = "⚡ Instant Tracking",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7F3D0)
                                )
                            }

                            Text(
                                text = campaign?.earnCoinsText ?: "Earn Coins on Every Order",
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Black,
                                color = White,
                                letterSpacing = 0.2.sp
                            )

                            Text(
                                text = campaign?.rateDetail ?: "Complete transaction to earn Coins directly in your wallet.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFD1FAE5),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // ── 3. TRUST & STATS STRIP (3-Pillar Confidence) ─────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    Triple("⏱️ Tracking", campaign?.trackingWindow ?: "Within 48h", Color(0xFFEFF6FF)),
                    Triple("🍪 Cookie", campaign?.cookieDuration ?: "30 Days", Color(0xFFFEF3C7)),
                    Triple("💳 Payout", "UPI & Bank", Color(0xFFECFDF5))
                ).forEach { (label, value, bg) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = White),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = value,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // ── 4. HOW IT WORKS (Visual 3-Step Flow) ──────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⚡ How to Earn in 3 Easy Steps",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Step 1: Activate
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFE0F2FE), CircleShape)
                                    .border(1.dp, Color(0xFFBAE6FD), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("👆", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "1. Activate",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap button\nbelow",
                                fontSize = 9.5.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp,
                                minLines = 2,
                                maxLines = 2
                            )
                        }

                        // Connector Arrow 1 (aligned with circle center)
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "➔",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Step 2: Purchase
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFFEF3C7), CircleShape)
                                    .border(1.dp, Color(0xFFFDE68A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🛍️", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "2. Purchase",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Shop on\npartner site",
                                fontSize = 9.5.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp,
                                minLines = 2,
                                maxLines = 2
                            )
                        }

                        // Connector Arrow 2 (aligned with circle center)
                        Box(
                            modifier = Modifier
                                .height(42.dp)
                                .padding(horizontal = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "➔",
                                fontSize = 13.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Step 3: Get Coins
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFDCFCE7), CircleShape)
                                    .border(1.dp, Color(0xFFBBF7D0), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🪙", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "3. Get Coins",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Auto-added\nto wallet",
                                fontSize = 9.5.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 13.sp,
                                minLines = 2,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // ── 5. RATES & TRACKING DETAILS TAB ───────────────────────
            var showTrackingTab by remember { mutableStateOf(false) }

            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📊 Payout & Tracking Rates",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    // Pill Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(21.dp))
                            .padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (!showTrackingTab) Color(0xFF047857) else Color.Transparent,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { showTrackingTab = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Earn Rates",
                                color = if (!showTrackingTab) White else TextGray,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(
                                    if (showTrackingTab) Color(0xFF047857) else Color.Transparent,
                                    RoundedCornerShape(18.dp)
                                )
                                .clickable { showTrackingTab = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Timeline & Terms",
                                color = if (showTrackingTab) White else TextGray,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Content Rows
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (!showTrackingTab) {
                            rates.forEach { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = row.category,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDark,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFECFDF5), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = row.earnRate,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF047857)
                                        )
                                    }
                                }
                            }
                        } else {
                            trackings.take(1).forEach { row ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("⏱️ Tracking Time:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        Text(row.tracking, fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Bold)
                                    }
                                    HorizontalDivider(color = Color(0xFFE2E8F0))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("✔️ Coin Approval:", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        Text(row.confirmation, fontSize = 12.sp, color = Color(0xFF047857), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── 6. IMPORTANT TIPS & CHECKLIST ─────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "💡 Guidelines for 100% Tracking",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    guidelines.forEach { tip ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("✅", fontSize = 12.sp, modifier = Modifier.padding(top = 1.dp))
                            Text(
                                text = tip,
                                fontSize = 11.5.sp,
                                color = TextGray,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
