// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.UserSession

enum class HistoryType {
    EARNING,
    WITHDRAWAL
}

data class TransactionRecord(
    val id: String,
    val sourceName: String,
    val description: String,
    val coins: Long,
    val inrAmount: Double,
    val type: HistoryType,
    val statusText: String,
    val isCompleted: Boolean,
    val dateText: String,
    val emoji: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onBackClick: () -> Unit
) {
    val userSession = UserSession
    var selectedFilter by remember { mutableStateOf("All") }

    val transactions = remember(userSession.totalCoins, userSession.withdrawnCoins) {
        if (userSession.totalCoins == 0L && userSession.withdrawnCoins == 0L) {
            emptyList<TransactionRecord>()
        } else {
            listOf(
                TransactionRecord(
                    id = "RC-TX-8921",
                    sourceName = "Amazon India",
                    description = "Online Shopping Cashback",
                    coins = userSession.totalCoins,
                    inrAmount = userSession.totalCoins / 4.0,
                    type = HistoryType.EARNING,
                    statusText = "Coins Credited ✓",
                    isCompleted = true,
                    dateText = "Today • Just now",
                    emoji = "🛍️"
                )
            )
        }
    }

    val filteredList = transactions.filter { item ->
        when (selectedFilter) {
            "Earnings" -> item.type == HistoryType.EARNING
            "Withdrawals" -> item.type == HistoryType.WITHDRAWAL
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Order & Coin History", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                        Text("Track sources and withdrawal status", fontSize = 11.5.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
        ) {
            // ── Top Summary Header ──────────────────────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp,
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪙 Total Earned", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${userSession.totalCoins} Coins", fontSize = 16.sp, fontWeight = FontWeight.Black, color = DarkGreen)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFCBD5E1)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏳ Pending", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${userSession.pendingCoins} Coins", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFD97706))
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFCBD5E1)))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("💸 Withdrawn", fontSize = 11.sp, color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("${userSession.withdrawnCoins} Coins", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF2563EB))
                    }
                }
            }

            // ── Filter Chips ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All" to "All Activities", "Earnings" to "Where Coins Came From", "Withdrawals" to "UPI Withdrawals").forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) DarkGreen else Color.White)
                            .border(1.dp, if (isSelected) DarkGreen else Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = key }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextDark,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ── Transaction List / Empty State ─────────────────────────────
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("📜", fontSize = 42.sp)
                        Text(
                            text = "No Transactions Yet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Coins earned from partner shopping, loan approvals, cards, and UPI cashouts will appear here.",
                            fontSize = 12.5.sp,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(filteredList) { tx ->
                        val isEarning = tx.type == HistoryType.EARNING

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Emoji Avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (isEarning) Color(0xFFE8F5E9) else Color(0xFFEFF6FF),
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (isEarning) Color(0xFF81C784).copy(alpha = 0.4f) else Color(0xFF93C5FD).copy(alpha = 0.4f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(tx.emoji, fontSize = 20.sp)
                            }

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.sourceName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = tx.description,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Status badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                if (isEarning) Color(0xFFE8F5E9) else Color(0xFFEFF6FF),
                                                RoundedCornerShape(6.dp)
                                            )
                                            .padding(horizontal = 7.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = tx.statusText,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isEarning) DarkGreen else Color(0xFF1D4ED8)
                                        )
                                    }
                                    Text(
                                        text = tx.dateText,
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            }

                            // Coin Amount
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isEarning) "+${tx.coins}" else "-${tx.coins}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isEarning) DarkGreen else Color(0xFFDC2626)
                                )
                                Text(
                                    text = "₹${String.format("%.2f", tx.inrAmount)}",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
}


