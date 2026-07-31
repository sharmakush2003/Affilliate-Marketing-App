// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import kotlinx.coroutines.launch
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
import com.rewardclub.app.ui.theme.DarkGreen
import com.rewardclub.app.ui.theme.NavyDark
import com.rewardclub.app.ui.theme.TextGray
import com.rewardclub.app.ui.theme.BorderColor
import com.rewardclub.app.ui.theme.XYZTheme

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
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Step 3: Initialize Cuelinks SDK (reads Channel ID 301603 from AndroidManifest)
        com.cuelinks.sdk.Cuelinks.initialize(this)

        setContent {
            XYZTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onTimeout = { showSplash = false })
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppMainContainer()
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.4f) }
    val offsetY = remember { Animatable(400f) }
    val alpha = remember { Animatable(0f) }
    
    val overshootEasing = remember {
        Easing { fraction ->
            OvershootInterpolator(1.3f).getInterpolation(fraction)
        }
    }
    
    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = overshootEasing
                )
            )
        }
        launch {
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 1000,
                    easing = LinearOutSlowInEasing
                )
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 800
                )
            )
        }
        
        delay(2500) // Keep splash screen for 2.5s
        onTimeout()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F5132),
                        Color(0xFF198754)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .offset(y = offsetY.value.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    this.alpha = alpha.value
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.reward_club_logo),
                contentDescription = "Reward Club Logo",
                modifier = Modifier
                    .size(130.dp)
                    .shadow(12.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(16.dp)
            )
            
            Text(
                text = "Reward Club",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
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
    val items = listOf(
        DrawerItemData("Account Details", Icons.Default.AccountBox),
        DrawerItemData("Order History", Icons.Default.ShoppingBag),
        DrawerItemData("About Company", Icons.Default.Info),
        DrawerItemData("Help & Support", Icons.Default.Help)
    )

    ModalDrawerSheet(
        drawerContainerColor = Color(0xFFFAFAFA),
        modifier = Modifier.width(310.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Profile Header Section (Premium Dark Green Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF005C38), Color(0xFF003820))
                        )
                    )
                    .clickable { onItemClick("Header") }
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Profile Avatar Circle with Gold border
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color.White, shape = CircleShape)
                                .border(2.dp, Color(0xFFFF9900), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userName.take(1),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF005C38)
                            )
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = userName,
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text("👑", fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = userPhone,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Floating Mini Wallet Card in Drawer
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🪙", fontSize = 16.sp)
                            Text(
                                text = "Wallet Balance",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "$walletCoins Coins",
                            color = Color(0xFFFF9900),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Items List - Premium Custom Rows
            items.forEach { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp)
                        .clickable { onItemClick(item.title) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = Color(0xFF005C38),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = item.title,
                                color = Color(0xFF0F1111),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer Branding
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(color = Color(0xFFE5E5E5))
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.reward_club_logo),
                        contentDescription = "Reward Club Logo",
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                    )
                    Text(
                        text = "Reward Club",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "•  v3.2.1",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class DrawerItemData(val title: String, val icon: ImageVector)

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


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(onItemClick = { itemTitle ->
                scope.launch { drawerState.close() }
                when (itemTitle) {
                    "Header" -> {
                        navigateTo(Screen.Login)
                    }
                    "Account Details" -> {
                        navigateTo(Screen.AccountDetails)
                    }
                    "Order History" -> {
                        navigateTo(Screen.Products)
                    }
                    "About Company" -> {
                        navigateTo(Screen.AboutCompany)
                    }
                    "Help & Support" -> {
                        navigateTo(Screen.HelpSupport)
                    }
                }
            })
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
                when (currentScreen) {
                    is Screen.Home -> HomeScreen(
                        onHamburgerClick = { scope.launch { drawerState.open() } },
                        onJoinClick = { navigateTo(Screen.Login) },
                        onBrandClick = { brand -> navigateTo(Screen.EarnCoins(brand)) },
                        onCategoryClick = { category ->
                            when (category) {
                                "Products" -> navigateTo(Screen.Products)
                                "Vouchers" -> navigateTo(Screen.Vouchers)
                                "Utilities" -> navigateTo(Screen.Products)
                            }
                        },
                        onViewAllCouponsClick = { navigateTo(Screen.Coupons) },
                        onCouponClick = { coupon -> navigateTo(Screen.CouponDetail(coupon)) }
                    )
                    is Screen.EarnCoins -> EarnCoinsScreen(
                        brandName = (currentScreen as Screen.EarnCoins).brandName,
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
                        couponName = (currentScreen as Screen.CouponDetail).couponName,
                        onBackClick = { navigateBack() }
                    )
                    is Screen.HelpSupport -> HelpSupportScreen(
                        onBackClick = { navigateBack() }
                    )
                    is Screen.Login -> LoginScreen(
                        onLoginSuccess = { navigateBack() },
                        onBackClick = { navigateBack() }
                    )
                    is Screen.AccountDetails -> AccountDetailsScreen(
                        onBackClick = { navigateBack() }
                    )
                    is Screen.AboutCompany -> AboutCompanyScreen(
                        onBackClick = { navigateBack() }
                    )
                    is Screen.Profile -> AccountDetailsScreen(
                        onBackClick = { navigateTo(Screen.Home) }
                    )
                    else -> {}
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
            tonalElevation = 0.dp,
            modifier = Modifier.height(57.dp),
            windowInsets = WindowInsets(0.dp)
        ) {
            NavigationBarItem(
                selected = currentScreen is Screen.Home,
                onClick = { onTabSelected(Screen.Home) },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home", modifier = Modifier.size(22.dp)) },
                label = { Text("Home", fontSize = 10.sp, fontWeight = if (currentScreen is Screen.Home) FontWeight.Bold else FontWeight.Normal) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyDark,
                    selectedTextColor = NavyDark,
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
                    selectedIconColor = NavyDark,
                    selectedTextColor = NavyDark,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = Color(0xFFFFF3E0)
                )
            )
        }
    }
}


