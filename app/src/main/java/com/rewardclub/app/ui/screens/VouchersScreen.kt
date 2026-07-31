// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.rewardclub.app.R

data class VoucherGridItem(
    val name: String,
    val brandName: String,
    val discount: String,
    val iconText: String,
    val logoResId: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VouchersScreen(
    onBackClick: () -> Unit
) {
    val vouchers = listOf(
        VoucherGridItem("Amazon", "Amazon Pay", "2% OFF", "💳", R.drawable.amazon_logo),
        VoucherGridItem("Amazon Shopping...", "Amazon Shop", "2.25% OFF", "🛍️", R.drawable.amazon_logo),
        VoucherGridItem("Flipkart", "Flipkart Voucher", "2% OFF", "🛒", R.drawable.flipkart_logo),
        VoucherGridItem("HP Pay", "HP Pay Voucher", "0.75% OFF", "⛽", R.drawable.hp_pay_logo),
        VoucherGridItem("Myntra", "Myntra Voucher", "4% OFF", "👗", R.drawable.myntra_logo),
        VoucherGridItem("Swiggy Money...", "Swiggy Wallet", "3% OFF", "🍔", R.drawable.swiggy_logo)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vouchers", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = White,
                    titleContentColor = TextDark,
                    navigationIconContentColor = TextDark
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayBackground)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                // Filter pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkGreen)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✓", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Top discounted", color = White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(DarkGreen)
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text("✓", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("Top selling", color = White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                // Grid layout for Vouchers
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(vouchers) { voucher ->
                        VoucherGridCard(voucher = voucher)
                    }
                }
            }

            // Filters sticky bar at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(DarkGreen)
                    .border(1.dp, BorderColor, RoundedCornerShape(30.dp))
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.clickable { }
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                        Text("Filters", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Divider(
                        color = White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(16.dp)
                            .width(1.dp)
                    )
                    Text(
                        text = "Sort by",
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { }
                    )
                }
            }
        }
    }
}

@Composable
fun VoucherGridCard(voucher: VoucherGridItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (voucher.logoResId != null) {
                    Image(
                        painter = painterResource(id = voucher.logoResId),
                        contentDescription = voucher.name,
                        modifier = Modifier.size(32.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = voucher.iconText,
                        fontSize = 24.sp
                    )
                }
                if (voucher.discount.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AccentGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = voucher.discount,
                            color = Color.Black,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = voucher.name,
                    color = TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = voucher.brandName,
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
