// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import com.rewardclub.app.utils.Supabase
import com.rewardclub.app.utils.DbProfile
import com.rewardclub.app.utils.UserSession
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.put
import kotlinx.serialization.json.buildJsonObject
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Redeem
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.delay
import androidx.compose.animation.core.*
import android.view.animation.OvershootInterpolator
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.screens.*
import com.rewardclub.app.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog

sealed class Screen {
    object Home : Screen()
    data class EarnCoins(val brandName: String) : Screen()
    object Products : Screen()
    object Vouchers : Screen()
    object Coupons : Screen()
    data class CouponDetail(val couponName: String) : Screen()
    object HelpSupport : Screen()
    object Login : Screen()
    object AccountDetails : Screen()
    object AboutCompany : Screen()
    object Profile : Screen()
    object Brands : Screen()
    object CreditCards : Screen()
    object PersonalLoans : Screen()
    object CarLoans : Screen()
    object TwoWheelerLoans : Screen()
    object Insurance : Screen()
    object ContactUs : Screen()
    object TransactionHistory : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize UserSession with Application Context & restore persisted session
        com.rewardclub.app.utils.UserSession.init(applicationContext)
        lifecycleScope.launch {
            com.rewardclub.app.utils.UserSession.listenToSession()
        }
        // Unified Financial API Architecture - zero legacy affiliate network dependencies

        // Handle deep link if app was opened via email verification link
        handleDeepLink(intent)

        setContent {
            XYZTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppMainContainer()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent) {
        val uri: Uri = intent.data ?: return
        if (uri.scheme != "rewardclub") return

        // Parse fragment or query string (format: rewardclub://home#access_token=XXX or rewardclub://home?access_token=XXX)
        val rawData = uri.fragment ?: uri.query ?: return
        val params = rawData.split("&").associate {
            val parts = it.split("=", limit = 2)
            val key = parts.getOrNull(0) ?: ""
            val rawVal = parts.getOrNull(1) ?: ""
            val valDecoded = try { java.net.URLDecoder.decode(rawVal, "UTF-8") } catch (e: Exception) { rawVal }
            key to valDecoded
        }

        val accessToken = params["access_token"] ?: return

        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Import the access token into the Supabase SDK session
                Supabase.client.auth.importAuthToken(accessToken)

                // Retrieve user profile (fall back to retrieveUser if currentUserOrNull returns null initially)
                val user = try {
                    Supabase.client.auth.retrieveUser(accessToken)
                } catch (e: Exception) {
                    Supabase.client.auth.currentUserOrNull()
                } ?: return@launch

                // Read pending registration data from SharedPreferences
                val prefs = getSharedPreferences("pending_registration", Context.MODE_PRIVATE)
                val pendingName = prefs.getString("full_name", "") ?: ""
                val pendingMobile = prefs.getString("mobile", "") ?: ""
                val pendingEmail = prefs.getString("email", user.email ?: "") ?: ""

                if (pendingName.isNotEmpty()) {
                    try {
                        // Update auth user metadata
                        Supabase.client.auth.updateUser {
                            data = buildJsonObject {
                                put("full_name", pendingName)
                                put("mobile", pendingMobile)
                                put("name", pendingName)
                            }
                        }
                        // Upsert profile with real name and mobile
                        Supabase.client.postgrest["profiles"].upsert(
                            DbProfile(
                                id = user.id,
                                email = pendingEmail,
                                full_name = pendingName,
                                mobile = pendingMobile,
                                total_coins = 0L,
                                redeemed_coins = 0L,
                                total_savings = 0L
                            )
                        )
                        // Clear pending registration data
                        prefs.edit().clear().apply()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                // Check database profile for full_name if pendingName was empty
                var displayName = pendingName
                if (displayName.isEmpty()) {
                    displayName = try {
                        val dbProf = Supabase.client.postgrest["profiles"]
                            .select { filter { eq("id", user.id) } }
                            .decodeSingleOrNull<DbProfile>()
                        dbProf?.full_name ?: ""
                    } catch (e: Exception) {
                        ""
                    }
                }
                if (displayName.isEmpty()) {
                    displayName = user.userMetadata?.get("full_name")?.toString()?.trim('"') 
                        ?: user.email?.substringBefore("@") 
                        ?: "Member"
                }

                // Auto-login to UserSession
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    UserSession.login(
                        userEmail = user.email ?: pendingEmail,
                        uid = user.id,
                        name = displayName
                    )
                    Toast.makeText(
                        this@MainActivity,
                        "Email Verified! Welcome to Reward Club 🎉",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

@Composable
fun DrawerBubbleItem(
    iconVector: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    subtitle: String? = null,
    badgeText: String? = null,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFFEEF2FF) else Color.Transparent,
        border = if (selected) BorderStroke(1.dp, Color(0xFFC7D2FE)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(2.dp, RoundedCornerShape(12.dp), ambientColor = iconBg, spotColor = iconBg)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = label,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                    if (!badgeText.isNullOrEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = badgeText,
                                color = Color(0xFFD97706),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun DrawerContent(
    userName: String = "",
    userPhone: String = "",
    walletCoins: String = "0",
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.Transparent,
        drawerShape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp),
        modifier = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color.White)
                .border(
                    BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            val drawerScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(drawerScrollState)
            ) {
                // ── Hero Profile Header ───────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A), // Midnight Slate
                                    Color(0xFF1E1B4B), // Deep Royal Indigo
                                    Color(0xFF312E81)  // Sapphire Indigo
                                )
                            )
                        )
                        .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // User Profile Info Row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onItemClick("Header") }
                        ) {
                            // Avatar Circle with Golden Gradient Ring
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .shadow(8.dp, CircleShape, spotColor = Color(0xFFFFD700))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                                        ),
                                        CircleShape
                                    )
                                    .padding(3.dp)
                                    .background(Color(0xFF0F172A), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (userName.contains("Guest") || userName.isEmpty()) "👤" else userName.take(1).uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = userName.ifEmpty { "Reward Club Member" },
                                        color = Color.White,
                                        fontSize = 17.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text("✨", fontSize = 14.sp)
                                }
                                Text(
                                    text = userPhone.ifEmpty { "Tap to Sign In 🔑" },
                                    color = Color(0xFFC7D2FE),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Wallet Balance Banner Pill Card
                        Surface(
                            onClick = { onItemClick("Order History") },
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E293B).copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, Color(0xFF334155)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                Brush.linearGradient(
                                                    listOf(Color(0xFFFEF3C7), Color(0xFFFDE047))
                                                ),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🪙", fontSize = 16.sp)
                                    }
                                    Column {
                                        Text(
                                            text = "Wallet Balance",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "$walletCoins Coins",
                                            color = Color(0xFFFACC15),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "History",
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── SECTION 1: EXPLORE & OFFERS ────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 20.dp, top = 6.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 12.dp)
                            .background(Color(0xFF4F46E5), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "EXPLORE & OFFERS",
                        color = Color(0xFF64748B),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }

                DrawerBubbleItem(
                    iconVector = Icons.Default.Home,
                    iconBg = Color(0xFFEEF2FF),
                    iconTint = Color(0xFF4F46E5),
                    label = "Home",
                    subtitle = "Explore top deals & rewards",
                    onClick = { onItemClick("Home") }
                )

                DrawerBubbleItem(
                    iconVector = Icons.Default.ShoppingBag,
                    iconBg = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF16A34A),
                    label = "Shop & Brands",
                    subtitle = "Earn cashback on top brands",
                    badgeText = "HOT",
                    onClick = { onItemClick("Shop") }
                )

                DrawerBubbleItem(
                    iconVector = Icons.Default.CreditCard,
                    iconBg = Color(0xFFF3E8FF),
                    iconTint = Color(0xFF7C3AED),
                    label = "Cards & Loans",
                    subtitle = "Instant approvals & high rewards",
                    onClick = { onItemClick("Cards") }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                    color = Color(0xFFF1F5F9)
                )

                // ── SECTION 2: MY ACCOUNT ──────────────────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 12.dp)
                            .background(Color(0xFF0284C7), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "MY ACCOUNT",
                        color = Color(0xFF64748B),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }

                DrawerBubbleItem(
                    iconVector = Icons.Default.AccountCircle,
                    iconBg = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0284C7),
                    label = "Account Details",
                    subtitle = "Manage profile & payout UPI",
                    onClick = { onItemClick("Account Details") }
                )

                DrawerBubbleItem(
                    iconVector = Icons.Default.ReceiptLong,
                    iconBg = Color(0xFFFFEDD5),
                    iconTint = Color(0xFFEA580C),
                    label = "Order & Coin History",
                    subtitle = "Track earnings & withdrawals",
                    onClick = { onItemClick("Order History") }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp),
                    color = Color(0xFFF1F5F9)
                )

                // ── SECTION 3: SUPPORT & INFORMATION ──────────────────────────
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 20.dp, top = 4.dp, bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 3.dp, height = 12.dp)
                            .background(Color(0xFFCA8A04), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = "SUPPORT & INFO",
                        color = Color(0xFF64748B),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    )
                }

                DrawerBubbleItem(
                    iconVector = Icons.Default.Call,
                    iconBg = Color(0xFFECFDF5),
                    iconTint = Color(0xFF059669),
                    label = "Contact Us",
                    subtitle = "Support email & callback",
                    badgeText = "24x7",
                    onClick = { onItemClick("Contact Us") }
                )

                DrawerBubbleItem(
                    iconVector = Icons.Default.Info,
                    iconBg = Color(0xFFFEF9C3),
                    iconTint = Color(0xFFCA8A04),
                    label = "About Company",
                    subtitle = "Learn about Reward Club",
                    onClick = { onItemClick("About Company") }
                )

                DrawerBubbleItem(
                    iconVector = Icons.AutoMirrored.Filled.Help,
                    iconBg = Color(0xFFFFE4E6),
                    iconTint = Color(0xFFE11D48),
                    label = "Help & FAQs",
                    subtitle = "Answers & support guides",
                    onClick = { onItemClick("Help & Support") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Footer Branding & Version ──────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .padding(vertical = 18.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🛍️", fontSize = 14.sp)
                        Text(
                            text = "Reward Club v${com.rewardclub.app.BuildConfig.VERSION_NAME}",
                            color = Color(0xFF475569),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Made with ❤️ in India • All Rights Reserved",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RewardClubSplashScreen(onSplashFinished: () -> Unit) {
    val scale = remember { androidx.compose.animation.core.Animatable(0.75f) }
    val alpha = remember { androidx.compose.animation.core.Animatable(0f) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Infinite bouncing animation for the 3D coin/gift loading object
    val infiniteTransition = rememberInfiniteTransition(label = "splashBounce")
    val bounceOffsetY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceOffsetY"
    )

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 900,
                    easing = LinearOutSlowInEasing
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700)
            )
        }
        val startTime = System.currentTimeMillis()
        val totalDuration = 2400L
        while (System.currentTimeMillis() - startTime < totalDuration) {
            val elapsed = System.currentTimeMillis() - startTime
            progress = (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f)
            delay(16)
        }
        progress = 1f
        delay(150)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFDF8), // Luxury Ivory Cream
                        Color(0xFFFAF4E8), // Soft Warm Cream
                        Color(0xFFF5EAD4)  // Golden Ivory Tint
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative Soft Ambient Background Glow Circles
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFFFDE68A).copy(alpha = 0.35f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .graphicsLayer(
                    scaleX = scale.value,
                    scaleY = scale.value,
                    alpha = alpha.value
                )
                .padding(24.dp)
        ) {
            // ── Hero Animated Bouncing Logo & 3D Coin Badge Container ─────────
            Box(
                modifier = Modifier.offset(y = bounceOffsetY.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer Shadow Circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .shadow(20.dp, CircleShape, spotColor = Color(0xFFD97706), ambientColor = Color(0xFFF59E0B))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFFD700), Color(0xFFFFA000), Color(0xFF4F46E5))
                            ),
                            CircleShape
                        )
                        .padding(4.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛍️",
                        fontSize = 58.sp
                    )
                }

                // Bouncing Gold Crown Coin Badge (Top Right Offset)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = 42.dp, y = (-38).dp)
                        .shadow(8.dp, CircleShape, spotColor = Color(0xFFFFD700))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFFEF3C7), Color(0xFFFDE047))
                            ),
                            CircleShape
                        )
                        .border(1.5.dp, Color(0xFFD97706), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🪙", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name in Deep Navy Slate & Warm Amber Accent
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "REWARD CLUB",
                    fontSize = 31.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    letterSpacing = 2.5.sp
                )
                Text("👑", fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "India's #1 Cashback & Financial Rewards Club",
                fontSize = 12.5.sp,
                color = Color(0xFF475569),
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // ── Animated Interactive Bouncing Object Loader Card ─────────────
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFF1E6D0)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .width(260.dp)
                    .padding(horizontal = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Animated Bouncing Reward Icons Object Row
                    Row(
                        modifier = Modifier.offset(y = (bounceOffsetY * 0.6f).dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("🪙", fontSize = 20.sp)
                        Text("🎁", fontSize = 24.sp)
                        Text("🪙", fontSize = 20.sp)
                    }

                    // Smooth Golden Progress Indicator Bar
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(50)),
                        color = Color(0xFFD97706),
                        trackColor = Color(0xFFFEF3C7)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Loading your experience...",
                            fontSize = 11.5.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            fontSize = 11.5.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Footer Tagline at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("✨", fontSize = 13.sp)
                Text(
                    text = "Exclusive Financial & Cashback Perks",
                    fontSize = 11.5.sp,
                    color = Color(0xFF78716C),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainContainer() {
    val context = LocalContext.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStack = remember { mutableStateListOf<Screen>(Screen.Home) }
    val currentScreen = backStack.lastOrNull() ?: Screen.Home

    fun navigateTo(screen: Screen) {
        if (screen is Screen.Home) {
            backStack.clear()
            backStack.add(Screen.Home)
        } else {
            backStack.add(screen)
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        navigateBack()
    }

    val homeScrollState = rememberScrollState()

    val userSession = com.rewardclub.app.utils.UserSession
    val currentUser = userSession.currentUser
    val isSessionChecked = userSession.isSessionChecked

    var splashFinished by rememberSaveable { mutableStateOf(false) }

    if (!splashFinished || !isSessionChecked) {
        RewardClubSplashScreen(
            onSplashFinished = {
                splashFinished = true
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                val isLoggedIn = currentUser != null
                DrawerContent(
                    userName = if (isLoggedIn) userSession.fullName.ifEmpty { "User" } else "Reward Club Guest",
                    userPhone = if (isLoggedIn) userSession.mobile.ifEmpty { userSession.email } else "Tap to Sign In 🔑",
                    walletCoins = if (isLoggedIn) userSession.totalCoins.toString() else "0",
                    onItemClick = { itemTitle ->
                        scope.launch { drawerState.close() }
                        when (itemTitle) {
                            "Home" -> navigateTo(Screen.Home)
                            "Shop" -> navigateTo(Screen.Brands)
                            "Cards" -> navigateTo(Screen.CreditCards)
                            "Header", "Account Details" -> {
                                if (isLoggedIn) navigateTo(Screen.AccountDetails) else navigateTo(Screen.Login)
                            }
                            "Order History" -> navigateTo(Screen.TransactionHistory)
                            "About Company" -> navigateTo(Screen.AboutCompany)
                            "Help & Support" -> navigateTo(Screen.HelpSupport)
                            "Contact Us" -> navigateTo(Screen.ContactUs)
                        }
                    }
                )
            }
        ) {
            Scaffold(
                bottomBar = {
                    if (currentScreen is Screen.Home || currentScreen is Screen.Profile || currentScreen is Screen.ContactUs || currentScreen is Screen.TransactionHistory) {
                        BottomNavigationBar(
                            currentScreen = currentScreen,
                            onTabSelected = { screen ->
                                navigateTo(screen)
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    fun runIfAuthenticated(action: () -> Unit) {
                        action()
                    }

                    when (currentScreen) {
                        is Screen.Home -> HomeScreen(
                            scrollState = homeScrollState,
                            onHamburgerClick = { scope.launch { drawerState.open() } },
                            onJoinClick = { navigateTo(Screen.Login) },
                            onBrandClick = { brand -> navigateTo(Screen.EarnCoins(brand)) },
                            onCategoryClick = { category ->
                                when (category) {
                                    "Shop", "Shopping" -> navigateTo(Screen.Brands)
                                    "Loan", "Loans", "Personal Loan" -> navigateTo(Screen.PersonalLoans)
                                    "Car Loan" -> navigateTo(Screen.CarLoans)
                                    "Two Wheeler Loan", "2-Wheeler Loan" -> navigateTo(Screen.TwoWheelerLoans)
                                    "Insurance" -> navigateTo(Screen.Insurance)
                                    "Credit Card", "Cards" -> navigateTo(Screen.CreditCards)
                                    else -> navigateTo(Screen.Brands)
                                }
                            },
                            onViewAllCouponsClick = { navigateTo(Screen.Coupons) },
                            onCouponClick = { coupon -> navigateTo(Screen.CouponDetail(coupon)) },
                            onViewAllBrandsClick = { navigateTo(Screen.Brands) },
                            onViewAllCardsClick = { navigateTo(Screen.CreditCards) },
                            onViewAllPersonalLoansClick = { navigateTo(Screen.PersonalLoans) },
                            onViewAllCarLoansClick = { navigateTo(Screen.CarLoans) },
                            onViewAllTwoWheelerLoansClick = { navigateTo(Screen.TwoWheelerLoans) },
                            onViewAllInsuranceClick = { navigateTo(Screen.Insurance) }
                        )
                        is Screen.Brands -> BrandsScreen(
                            onBackClick = { navigateBack() },
                            onBrandClick = { brand -> navigateTo(Screen.EarnCoins(brand)) }
                        )
                        is Screen.CreditCards -> CreditCardsScreen(
                            onBackClick = { navigateBack() },
                            onCardClick = { card -> navigateTo(Screen.EarnCoins(card)) }
                        )
                        is Screen.PersonalLoans -> PersonalLoansScreen(
                            onBackClick = { navigateBack() },
                            onLoanClick = { loan -> navigateTo(Screen.EarnCoins(loan)) }
                        )
                        is Screen.CarLoans -> CarLoansScreen(
                            onBackClick = { navigateBack() },
                            onLoanClick = { loan -> navigateTo(Screen.EarnCoins(loan)) }
                        )
                        is Screen.TwoWheelerLoans -> TwoWheelerLoansScreen(
                            onBackClick = { navigateBack() },
                            onLoanClick = { loan -> navigateTo(Screen.EarnCoins(loan)) }
                        )
                        is Screen.Insurance -> InsuranceScreen(
                            onBackClick = { navigateBack() },
                            onInsuranceClick = { insurance -> navigateTo(Screen.EarnCoins(insurance)) }
                        )
                        is Screen.EarnCoins -> EarnCoinsScreen(
                            brandName = currentScreen.brandName,
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Products -> ProductsScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Vouchers -> VouchersScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Coupons -> CouponsScreen(
                            onBackClick = { navigateBack() },
                            onCouponClick = { coupon -> navigateTo(Screen.CouponDetail(coupon)) }
                        )
                        is Screen.CouponDetail -> CouponDetailScreen(
                            couponName = currentScreen.couponName,
                            onBackClick = { navigateBack() }
                        )
                        is Screen.HelpSupport -> HelpSupportScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Login -> LoginScreen(
                            onLoginSuccess = {
                                navigateTo(Screen.Home)
                            },
                            onBackClick = {
                                navigateBack()
                            }
                        )
                        is Screen.AccountDetails -> AccountDetailsScreen(
                            onBackClick = { navigateBack() },
                            onContactUsClick = { navigateTo(Screen.ContactUs) },
                            onTransactionHistoryClick = { navigateTo(Screen.TransactionHistory) }
                        )
                        is Screen.AboutCompany -> AboutCompanyScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Profile -> AccountDetailsScreen(
                            onBackClick = { navigateTo(Screen.Home) },
                            onContactUsClick = { navigateTo(Screen.ContactUs) },
                            onTransactionHistoryClick = { navigateTo(Screen.TransactionHistory) }
                        )
                        is Screen.ContactUs -> ContactUsScreen(
                            onBackClick = { navigateBack() },
                            onNavigateToFaqs = { navigateTo(Screen.HelpSupport) }
                        )
                        is Screen.TransactionHistory -> TransactionHistoryScreen(
                            onBackClick = { navigateBack() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    Column {
        HorizontalDivider(color = BorderColor, thickness = 0.8.dp)
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            NavigationBarItem(
                selected = currentScreen is Screen.Home,
                onClick = { onTabSelected(Screen.Home) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
                label = { Text("Home", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Home) FontWeight.Bold else FontWeight.Normal) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextDark,
                    selectedTextColor = TextDark,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = Color(0xFFFFF3E0)
                )
            )
            NavigationBarItem(
                selected = currentScreen is Screen.Profile || currentScreen is Screen.ContactUs || currentScreen is Screen.TransactionHistory,
                onClick = { onTabSelected(Screen.Profile) },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(22.dp)) },
                label = { Text("Profile", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Profile || currentScreen is Screen.ContactUs || currentScreen is Screen.TransactionHistory) FontWeight.Bold else FontWeight.Normal) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TextDark,
                    selectedTextColor = TextDark,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = Color(0xFFFFF3E0)
                )
            )
        }
    }
}


