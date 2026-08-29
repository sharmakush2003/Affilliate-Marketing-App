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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Easing
import android.view.animation.OvershootInterpolator
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.screens.*
import com.rewardclub.app.ui.theme.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
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
    object Loans : Screen()
    object Insurance : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize UserSession and listen to session changes
        lifecycleScope.launch {
            com.rewardclub.app.utils.UserSession.listenToSession()
        }
        // Step 3: Initialize Cuelinks SDK (reads Channel ID 301603 from AndroidManifest)
        com.cuelinks.sdk.Cuelinks.initialize(this)

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

        // Parse fragment for access_token (format: rewardclub://home#access_token=XXX&refresh_token=YYY)
        val fragment = uri.fragment ?: return
        val params = fragment.split("&").associate {
            val parts = it.split("=", limit = 2)
            (parts.getOrNull(0) ?: "") to java.net.URLDecoder.decode(parts.getOrNull(1) ?: "", "UTF-8")
        }

        val accessToken = params["access_token"] ?: return
        val refreshToken = params["refresh_token"] ?: ""

        lifecycleScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                // Import the session into Supabase client
                val fullUri = "rewardclub://home#access_token=${accessToken}&refresh_token=${refreshToken}&token_type=bearer&type=signup"
                Supabase.client.auth.parseSessionFromUrl(fullUri)

                val user = Supabase.client.auth.currentUserOrNull() ?: return@launch

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

                // Auto-login to UserSession
                val name = pendingName.ifEmpty {
                    user.userMetadata?.get("full_name")?.toString()?.trim('"') ?: ""
                }
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    UserSession.login(
                        userEmail = user.email ?: pendingEmail,
                        uid = user.id,
                        name = name
                    )
                    android.widget.Toast.makeText(
                        this@MainActivity,
                        "Welcome to Reward Club! 🎉",
                        android.widget.Toast.LENGTH_SHORT
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
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) Color(0xFFE8F5E9) else Color.Transparent,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
    )
    val textColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) DarkGreen else TextDark.copy(alpha = 0.8f),
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            icon()
            Text(
                text = label,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp,
                color = textColor
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
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        modifier = Modifier.width(260.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(Color(0xFFFAFAFA))
                .border(
                    BorderStroke(1.5.dp, DarkGreen.copy(alpha = 0.3f)),
                    RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            val drawerScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(drawerScrollState)
            ) {
                // Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(DarkGreen.copy(alpha=0.15f), Color.Transparent)
                            )
                        )
                        .clickable { onItemClick("Header") }
                        .padding(vertical = 32.dp, horizontal = 24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, DarkGreen, CircleShape)
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (userName == "Join Reward Club") "👤" else userName.take(1).uppercase(),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = userName,
                                    color = DarkGreen,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("👑", fontSize = 12.sp)
                            }
                            Text(
                                text = userPhone,
                                color = TextDark.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🪙", fontSize = 12.sp)
                                Text(text = "$walletCoins Coins", color = Color(0xFFFF9900), fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sections
                Text(
                    text = "PROFILE & ORDERS",
                    color = TextDark.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp)
                )
                DrawerBubbleItem(
                    icon = { Icon(Icons.Default.AccountBox, contentDescription = null, tint = DarkGreen) },
                    label = "Account Details",
                    selected = false,
                    onClick = { onItemClick("Account Details") }
                )
                DrawerBubbleItem(
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = DarkGreen) },
                    label = "Order History",
                    selected = false,
                    onClick = { onItemClick("Order History") }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp), color = DarkGreen.copy(alpha = 0.2f))

                Text(
                    text = "SUPPORT",
                    color = TextDark.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp)
                )
                DrawerBubbleItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null, tint = DarkGreen) },
                    label = "About Company",
                    selected = false,
                    onClick = { onItemClick("About Company") }
                )
                DrawerBubbleItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Help, contentDescription = null, tint = DarkGreen) },
                    label = "Help & Support",
                    selected = false,
                    onClick = { onItemClick("Help & Support") }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Footer Branding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "App version ${com.rewardclub.app.BuildConfig.VERSION_NAME}",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Made with ❤️ in India",
                        color = Color.Gray,
                        fontSize = 10.sp
                    )
                }
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


    val userSession = com.rewardclub.app.utils.UserSession
    val currentUser = userSession.currentUser
    val isSessionChecked = userSession.isSessionChecked
    var showLoginRequiredDialog by remember { mutableStateOf(false) }
    var showGuestDisclaimerDialog by remember { mutableStateOf(false) }
    var pendingGuestAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    if (showGuestDisclaimerDialog) {
        Dialog(
            onDismissRequest = {
                showGuestDisclaimerDialog = false
                pendingGuestAction = null
            }
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Premium Emoji Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFFFF3E0), CircleShape)
                            .border(1.5.dp, Color(0xFFFF9900).copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛍️", fontSize = 32.sp)
                    }

                    // Title
                    Text(
                        text = "Shopping as Guest",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    // Description text
                    Text(
                        text = "You can shop through this link, but you will not earn any Reward Coins on your transaction. Sign in now to get cashback coins! 🪙",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons (Centered & Stacked for clean styling and responsiveness)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Primary "Proceed to Shop" button
                        Button(
                            onClick = {
                                showGuestDisclaimerDialog = false
                                pendingGuestAction?.invoke()
                                pendingGuestAction = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Proceed to Shop 🚀", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // 2. Secondary "Sign In to Earn" outlined button
                        OutlinedButton(
                            onClick = {
                                showGuestDisclaimerDialog = false
                                pendingGuestAction = null
                                userSession.isGuest = false
                            },
                            border = BorderStroke(1.5.dp, DarkGreen),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Sign In to Earn 🪙", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        // 3. Cancel button
                        TextButton(
                            onClick = {
                                showGuestDisclaimerDialog = false
                                pendingGuestAction = null
                            }
                        ) {
                            Text("Cancel", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    if (showLoginRequiredDialog) {
        Dialog(
            onDismissRequest = { showLoginRequiredDialog = false }
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Premium Lock Emoji Badge
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFE8F5E9), CircleShape)
                            .border(1.5.dp, DarkGreen.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔒", fontSize = 32.sp)
                    }

                    // Title
                    Text(
                        text = "Sign In Required",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    // Description text
                    Text(
                        text = "Please sign in or create an account to start earning coins, viewing coupons, and ordering products. 🪙",
                        fontSize = 14.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showLoginRequiredDialog = false
                                userSession.isGuest = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Sign In Now 🔑", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        TextButton(
                            onClick = { showLoginRequiredDialog = false }
                        ) {
                            Text("Cancel", color = TextGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }

    if (!isSessionChecked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White, CircleShape)
                        .border(1.5.dp, DarkGreen, CircleShape)
                        .shadow(8.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "RC",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkGreen
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Reward Club",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Loading your session...",
                    fontSize = 14.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    color = DarkGreen,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    } else if (currentUser == null && !userSession.isGuest) {
        LoginScreen(
            onLoginSuccess = {
                // currentUser will automatically become non-null and trigger recomposition to main app
            },
            onBackClick = {
                (context as? android.app.Activity)?.finish()
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                val isLoggedIn = !userSession.isGuest && currentUser != null
                DrawerContent(
                    userName = if (isLoggedIn) userSession.fullName.ifEmpty { "User" } else "Join Reward Club",
                    userPhone = if (isLoggedIn) userSession.mobile.ifEmpty { userSession.email } else "Tap to Sign In",
                    walletCoins = if (isLoggedIn) userSession.totalCoins.toString() else "0",
                    onItemClick = { itemTitle ->
                        scope.launch { drawerState.close() }
                        if (!isLoggedIn && !userSession.isGuest && (itemTitle == "Header" || itemTitle == "Account Details" || itemTitle == "Order History")) {
                            showLoginRequiredDialog = true
                            return@DrawerContent
                        } else if (userSession.isGuest && itemTitle == "Order History") {
                            showLoginRequiredDialog = true
                            return@DrawerContent
                        }
                        when (itemTitle) {
                            "Header" -> navigateTo(Screen.AccountDetails)
                            "Account Details" -> navigateTo(Screen.AccountDetails)
                            "Order History" -> navigateTo(Screen.Products)
                            "About Company" -> navigateTo(Screen.AboutCompany)
                            "Help & Support" -> navigateTo(Screen.HelpSupport)
                        }
                    }
                )
            }
        ) {
            Scaffold(
                bottomBar = {
                    if (currentScreen is Screen.Home || currentScreen is Screen.Profile) {
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
                    val isGuest = userSession.isGuest
                    fun runIfAuthenticated(action: () -> Unit) {
                        if (isGuest) {
                            pendingGuestAction = action
                            showGuestDisclaimerDialog = true
                        } else {
                            action()
                        }
                    }

                    when (currentScreen) {
                        is Screen.Home -> HomeScreen(
                            onHamburgerClick = { scope.launch { drawerState.open() } },
                            onJoinClick = { userSession.isGuest = false },
                            onBrandClick = { brand -> runIfAuthenticated { navigateTo(Screen.EarnCoins(brand)) } },
                            onCategoryClick = { category ->
                                runIfAuthenticated {
                                    when (category) {
                                        "Products" -> navigateTo(Screen.Products)
                                        "Vouchers" -> navigateTo(Screen.Vouchers)
                                        "Utilities" -> navigateTo(Screen.Products)
                                    }
                                }
                            },
                            onViewAllCouponsClick = { runIfAuthenticated { navigateTo(Screen.Coupons) } },
                            onCouponClick = { coupon -> runIfAuthenticated { navigateTo(Screen.CouponDetail(coupon)) } },
                            onViewAllBrandsClick = { navigateTo(Screen.Brands) },
                            onViewAllCardsClick = { navigateTo(Screen.CreditCards) },
                            onViewAllLoansClick = { navigateTo(Screen.Loans) },
                            onViewAllInsuranceClick = { navigateTo(Screen.Insurance) }
                        )
                        is Screen.Brands -> BrandsScreen(
                            onBackClick = { navigateBack() },
                            onBrandClick = { brand -> runIfAuthenticated { navigateTo(Screen.EarnCoins(brand)) } }
                        )
                        is Screen.CreditCards -> CreditCardsScreen(
                            onBackClick = { navigateBack() },
                            onCardClick = { card -> runIfAuthenticated { navigateTo(Screen.EarnCoins(card)) } }
                        )
                        is Screen.Loans -> LoansScreen(
                            onBackClick = { navigateBack() },
                            onLoanClick = { loan -> runIfAuthenticated { navigateTo(Screen.EarnCoins(loan)) } }
                        )
                        is Screen.Insurance -> InsuranceScreen(
                            onBackClick = { navigateBack() },
                            onInsuranceClick = { insurance -> runIfAuthenticated { navigateTo(Screen.EarnCoins(insurance)) } }
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
                        is Screen.Login -> {
                            // No-op - login handled outside this container
                        }
                        is Screen.AccountDetails -> AccountDetailsScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.AboutCompany -> AboutCompanyScreen(
                            onBackClick = { navigateBack() }
                        )
                        is Screen.Profile -> AccountDetailsScreen(
                            onBackClick = { navigateTo(Screen.Home) }
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
                selected = currentScreen is Screen.Profile,
                onClick = { onTabSelected(Screen.Profile) },
                icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", modifier = Modifier.size(22.dp)) },
                label = { Text("Profile", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Profile) FontWeight.Bold else FontWeight.Normal) },
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


