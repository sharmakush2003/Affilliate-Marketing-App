package com.example.xyz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.xyz.ui.screens.*
import com.example.xyz.ui.theme.DarkGreen
import com.example.xyz.ui.theme.XYZTheme

sealed class Screen {
    object Home : Screen()
    data class EarnCoins(val brandName: String) : Screen()
    object Products : Screen()
    object Vouchers : Screen()
    object Coupons : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppMainContainer() {
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

    Scaffold(
        bottomBar = {
            if (currentScreen is Screen.Home || currentScreen is Screen.Coupons) {
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
                    onBrandClick = { brand -> navigateTo(Screen.EarnCoins(brand)) },
                    onCategoryClick = { category ->
                        when (category) {
                            "Products" -> navigateTo(Screen.Products)
                            "Vouchers" -> navigateTo(Screen.Vouchers)
                            "Utilities" -> navigateTo(Screen.Products) // map utility to products for now or custom
                        }
                    },
                    onViewAllCouponsClick = { navigateTo(Screen.Coupons) }
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
                    onBackClick = { navigateBack() }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onTabSelected(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Products, // map Shop to Products
            onClick = { onTabSelected(Screen.Products) },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Shop") },
            label = { Text("Shop") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Vouchers, // map Spend to Vouchers
            onClick = { onTabSelected(Screen.Vouchers) },
            icon = { Icon(Icons.Default.Redeem, contentDescription = "Spend") },
            label = { Text("Spend") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Coupons,
            onClick = { onTabSelected(Screen.Coupons) },
            icon = { Icon(Icons.Default.LocalActivity, contentDescription = "Coupons") },
            label = { Text("Coupons") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkGreen,
                selectedTextColor = DarkGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFE8F5E9)
            )
        )
    }
}
