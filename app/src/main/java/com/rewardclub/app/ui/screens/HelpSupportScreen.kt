// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*

data class FAQItem(
    val category: String,
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val faqItems = remember {
        listOf(
            // Coins
            FAQItem(
                category = "Coins & Rewards",
                question = "How do I earn XYZ Coins?",
                answer = "You earn coins by shopping at your favorite partner brands (like Amazon, Flipkart, Myntra, etc.) through the XYZ app. Simply click on the brand, check the rate table, tap 'Proceed to Earn Coins', and complete your purchase in the opened browser window."
            ),
            FAQItem(
                category = "Coins & Rewards",
                question = "What is the coin exchange rate?",
                answer = "In the XYZ rewards ecosystem, 4 Coins equal 1 Rupee (4 Coins = ₹1). Your balance is automatically updated in real-time."
            ),
            FAQItem(
                category = "Coins & Rewards",
                question = "How long does it take to track my coins?",
                answer = "Usually, your coins will track in 'Pending' status within 3 days of order shipping (or 3-5 days for Flipkart). It takes up to 60-100 days to verify and change status to 'Confirmed' to safeguard against returns."
            ),
            // Redemption
            FAQItem(
                category = "Redemption",
                question = "Where can I redeem my coins?",
                answer = "You can redeem coins in the 'Redeem coins on' section of the home page. You can purchase products, get discount coupons, buy brand e-vouchers, or pay for utilities."
            ),
            FAQItem(
                category = "Redemption",
                question = "Are there any charges for coupon codes?",
                answer = "No, most promotional coupons listed under the Coupons tab are absolutely free (0 coins) and require no cash payment."
            ),
            // Account
            FAQItem(
                category = "Account & Security",
                question = "Can I link multiple phone numbers?",
                answer = "No, only one phone number can be registered per account to maintain security. You cannot transfer coins to another number."
            ),
            FAQItem(
                category = "Account & Security",
                question = "What if my transaction is not tracked?",
                answer = "If your transaction is not tracked within 7 days, please raise a support ticket under the Help section with invoice details. Make sure your browser settings allow cookies when performing the purchase."
            )
        )
    }

    val categories = listOf("All", "Coins & Rewards", "Redemption", "Account & Security")

    val filteredFAQs = faqItems.filter { item ->
        val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
        val matchesSearch = item.question.contains(searchQuery, ignoreCase = true) ||
                item.answer.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQs & Support", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayBackground)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search questions...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = DarkGreen,
                    unfocusedBorderColor = BorderColor
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) DarkGreen else White)
                            .border(1.dp, if (isSelected) DarkGreen else BorderColor, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) White else TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // FAQs List view
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredFAQs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No questions found matching your search.",
                            color = TextGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    filteredFAQs.forEach { faq ->
                        FAQExpandableCard(faq = faq)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FAQExpandableCard(faq: FAQItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = faq.question,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextDark,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Divider(color = BorderColor.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
