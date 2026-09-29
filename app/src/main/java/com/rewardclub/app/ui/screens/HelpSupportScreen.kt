// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import com.rewardclub.app.utils.Supabase
import com.rewardclub.app.utils.UserSession
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class FeedbackFormQuery(
    val name: String,
    val email: String,
    val mobile: String,
    val user_id: String,
    val feedback_type: String,
    val rating: Int,
    val message: String
)

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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userSession = UserSession

    var selectedTab by remember { mutableIntStateOf(1) } // 0 = Contact & Feedback (Coming Soon), 1 = FAQs

    // Feedback Form State
    var userName by remember { mutableStateOf(userSession.fullName) }
    var userEmail by remember { mutableStateOf(userSession.email) }
    var userPhone by remember { mutableStateOf(userSession.mobile) }
    var feedbackType by remember { mutableStateOf("General Feedback") }
    var rating by remember { mutableIntStateOf(5) }
    var feedbackMessage by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userSession.fullName, userSession.email, userSession.mobile) {
        if (userName.isEmpty()) userName = userSession.fullName
        if (userEmail.isEmpty()) userEmail = userSession.email
        if (userPhone.isEmpty()) userPhone = userSession.mobile
    }

    // FAQ State
    var faqSearchQuery by remember { mutableStateOf("") }
    var selectedFaqCategory by remember { mutableStateOf("All") }

    val faqItems = remember {
        listOf(
            FAQItem(
                category = "Coins & Rewards",
                question = "How do I earn Reward Coins?",
                answer = "Simply select any partner brand (Amazon, Flipkart, HDFC, Axis, etc.), review the payout rate, and tap 'Proceed to Store / Apply'. Your transaction is tracked directly through our Unified Financial APIs and coins are credited to your wallet."
            ),
            FAQItem(
                category = "Coins & Rewards",
                question = "What is the coin conversion rate?",
                answer = "In Reward Club, 4 Coins = ₹1 (e.g. 1,000 Coins = ₹250). Coins can be withdrawn instantly to your bank account via UPI."
            ),
            FAQItem(
                category = "Withdrawals",
                question = "How do I withdraw coins via UPI?",
                answer = "Go to your Profile / Dashboard screen, enter your valid UPI ID (e.g. yourname@okhdfcbank or 9876543210@paytm), enter the coin amount (min 100 coins = ₹25), and tap 'Request UPI Transfer'. Transfers are processed directly to your linked UPI bank account."
            ),
            FAQItem(
                category = "Withdrawals",
                question = "How long does a UPI withdrawal take?",
                answer = "UPI withdrawals are reviewed and credited within 2 to 24 hours of submission. You can track status anytime under 'Order & Coin History'."
            ),
            FAQItem(
                category = "Account & Security",
                question = "Do I need to sign in to browse offers?",
                answer = "No! You can freely browse all 4 categories (Shop, Loans, Credit Cards, Insurance) and partner deals without any blocking login prompts."
            )
        )
    }

    val faqCategories = listOf("All", "Coins & Rewards", "Withdrawals", "Account & Security")

    val filteredFAQs = faqItems.filter { item ->
        val matchesCategory = selectedFaqCategory == "All" || item.category == selectedFaqCategory
        val matchesSearch = item.question.contains(faqSearchQuery, ignoreCase = true) ||
                item.answer.contains(faqSearchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Contact Us", fontWeight = FontWeight.Bold, fontSize = 19.sp, color = TextDark) },
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
            // ── Primary Navigation Tabs ─────────────────────────────────────
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = White,
                contentColor = DarkGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = DarkGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("💬", fontSize = 14.sp)
                            Text("Contact & Feedback", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("❓", fontSize = 14.sp)
                            Text("FAQs & Help", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                )
            }

            if (selectedTab == 0) {
                // â”€â”€ TAB 1: CONTACT & FEEDBACK (COMING SOON) â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE))
                                        ),
                                        CircleShape
                                    )
                                    .border(2.dp, Color(0xFF93C5FD).copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("â³", fontSize = 32.sp)
                            }

                            Text(
                                text = "Coming Soon",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )

                            Text(
                                text = "Contact & Feedback is currently being updated. Please check back soon!",
                                fontSize = 13.5.sp,
                                color = Color(0xFF64748B),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            } else {
                // ── TAB 2: FAQS ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    OutlinedTextField(
                        value = faqSearchQuery,
                        onValueChange = { faqSearchQuery = it },
                        placeholder = { Text("Search frequently asked questions...", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = White,
                            unfocusedContainerColor = White,
                            focusedBorderColor = DarkGreen,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        faqCategories.forEach { cat ->
                            val isSelected = selectedFaqCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) DarkGreen else White)
                                    .border(1.dp, if (isSelected) DarkGreen else Color(0xFFCBD5E1), RoundedCornerShape(20.dp))
                                    .clickable { selectedFaqCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) White else TextDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (filteredFAQs.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No questions found matching your search.", color = Color(0xFF64748B), fontSize = 14.sp)
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
    }

    // Feedback Submitted Success Dialog
    if (showSuccessDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showSuccessDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color(0xFFE8F5E9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📩", fontSize = 28.sp)
                    }
                    Text("Feedback Received!", fontWeight = FontWeight.Black, fontSize = 18.sp, color = TextDark, textAlign = TextAlign.Center)
                    Text(
                        "Thank you for reaching out! Your response has been safely delivered to our admin dashboard. Our support team will review it shortly.",
                        fontSize = 13.5.sp,
                        color = Color(0xFF475569),
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                    Button(
                        onClick = { showSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
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
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
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
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 13.sp,
                        color = Color(0xFF475569),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
