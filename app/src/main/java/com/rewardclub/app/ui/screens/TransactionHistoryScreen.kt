// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*

data class TransactionItem(
    val id: String,
    val title: String,
    val timestamp: String,
    val amount: Int, // e.g. -91 or +91
    val status: String, // "Burned", "Blocked", "Burned_reversed", "Earned", "Expired", "Redeemed"
    val category: String // "Earned", "Expired", "Redeemed"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit
) {
    // Selected Filter Tab: 0 = All, 1 = Earned, 2 = Expired, 3 = Redeemed
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    val filterTabs = listOf("All", "Earned", "Expired", "Redeemed")

    // Mock/Persisted Transactions exactly matching the screenshot
    val allTransactions = remember {
        listOf(
            TransactionItem(
                id = "tx1",
                title = "RECHARGE WORLD",
                timestamp = "23 Aug 2026 08:40:02 pm",
                amount = -91,
                status = "Burned",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx2",
                title = "RECHARGE WORLD",
                timestamp = "23 Aug 2026 08:40:02 pm",
                amount = -91,
                status = "Blocked",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx3",
                title = "RECHARGE WORLD",
                timestamp = "12 Aug 2026 06:31:30 pm",
                amount = -91,
                status = "Burned",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx4",
                title = "RECHARGE WORLD",
                timestamp = "12 Aug 2026 06:31:30 pm",
                amount = -91,
                status = "Blocked",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx5",
                title = "Rewardeez",
                timestamp = "26 Jul 2026 05:22:35 pm",
                amount = 91,
                status = "Burned_reversed",
                category = "Earned"
            ),
            TransactionItem(
                id = "tx6",
                title = "Rewardeez",
                timestamp = "26 Jul 2026 05:22:35 pm",
                amount = -91,
                status = "Burned",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx7",
                title = "Rewardeez",
                timestamp = "26 Jul 2026 05:22:35 pm",
                amount = -91,
                status = "Blocked",
                category = "Redeemed"
            ),
            TransactionItem(
                id = "tx8",
                title = "Amazon Shopping Cashback",
                timestamp = "15 Jul 2026 02:14:10 pm",
                amount = 250,
                status = "Earned",
                category = "Earned"
            ),
            TransactionItem(
                id = "tx9",
                title = "Flipkart Promo Payout",
                timestamp = "04 Jul 2026 11:05:44 am",
                amount = -100,
                status = "Expired",
                category = "Expired"
            )
        )
    }

    val filteredTransactions = remember(selectedFilterIndex, allTransactions) {
        when (selectedFilterIndex) {
            1 -> allTransactions.filter { it.category == "Earned" || it.amount > 0 }
            2 -> allTransactions.filter { it.category == "Expired" || it.status == "Expired" }
            3 -> allTransactions.filter { it.category == "Redeemed" || it.amount < 0 }
            else -> allTransactions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 44.dp, bottom = 16.dp, start = 12.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Transaction History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
        }

        // ── Filter Segmented Control Bar ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEFEFEF))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                filterTabs.forEachIndexed { index, tabName ->
                    val isSelected = selectedFilterIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF046A38) else Color.Transparent)
                            .clickable { selectedFilterIndex = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabName,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFF475569)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Transactions List Card Container ──────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (filteredTransactions.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📄", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No transactions found in this category",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        filteredTransactions.forEachIndexed { idx, item ->
                            TransactionRowItem(item = item)
                            if (idx < filteredTransactions.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 14.dp),
                                    color = Color(0xFFE2E8F0),
                                    thickness = 0.8.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRowItem(item: TransactionItem) {
    val isPositive = item.amount > 0
    val amountColor = if (isPositive) Color(0xFF10B981) else Color(0xFFEF4444)
    val statusColor = when (item.status.lowercase()) {
        "burned_reversed", "earned", "credited", "success" -> Color(0xFF10B981)
        else -> Color(0xFFEF4444)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Column: Title & Timestamp
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Text(
                text = item.timestamp,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right Column: Amount & Status
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isPositive) "+${item.amount}" else "${item.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )

                // Diamond / Coin Icon matching screenshot
                Text(
                    text = "🔰",
                    fontSize = 14.sp
                )
            }

            Text(
                text = item.status,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = statusColor
            )
        }
    }
}
