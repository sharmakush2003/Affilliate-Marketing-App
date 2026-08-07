// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInNew
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.*
import kotlinx.coroutines.launch

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

    val rates = when (brandName) {
        "Amazon" -> listOf(
            RateRow("Apparel", "Upto 12 coins per ₹100"),
            RateRow("Beauty & Luxury Beauty", "Upto 12 coins per ₹100"),
            RateRow("Grocery", "Upto 6 coins per ₹100"),
            RateRow("Luggage", "Upto 10 coins per ₹100"),
            RateRow("Shoes, Handbags & Accessories", "Upto 10 coins per ₹100"),
            RateRow("Watches", "Upto 9 coins per ₹100"),
            RateRow("Sports", "Upto 8 coins per ₹100"),
            RateRow("Lawn and Garden", "Upto 8 coins per ₹100"),
            RateRow("BISS (Industrial)", "Upto 6 coins per ₹100"),
            RateRow("Furniture", "Upto 8 coins per ₹100"),
            RateRow("Home & Home Improvement", "Upto 6 coins per ₹100"),
            RateRow("Kitchen", "Upto 7 coins per ₹100"),
            RateRow("Automotive", "Upto 7 coins per ₹100"),
            RateRow("Pet", "Upto 6 coins per ₹100"),
            RateRow("Baby", "Upto 6 coins per ₹100"),
            RateRow("Health & Personal Care", "Upto 6 coins per ₹100"),
            RateRow("Jewelry", "Upto 8 coins per ₹100"),
            RateRow("Appliances", "Upto 4 coins per ₹100"),
            RateRow("Books", "Upto 6 coins per ₹100"),
            RateRow("Toys", "Upto 5 coins per ₹100"),
            RateRow("Cell Phones & Accessories", "Upto 1 coin per ₹100")
        )
        "Flipkart" -> listOf(
            RateRow("Grocery", "Upto 3 coins per ₹100"),
            RateRow("Books & General Merch", "Upto 5 coins per ₹100"),
            RateRow("Home, Furniture & Fashion", "Upto 2 coins per ₹100"),
            RateRow("Large/Core/Emerging Electronics", "Upto 200 coins/txn"),
            RateRow("Kid's Footwear", "Upto 10 coins per ₹100"),
            RateRow("Mobile Tiers (Tiers 1-4)", "Upto 200 coins/txn")
        )
        else -> listOf(
            RateRow("General Purchases", "Upto 10 coins per ₹100")
        )
    }

    val trackings = when (brandName) {
        "Amazon" -> List(rates.size) {
            TrackRow("Within 3 days of shipping", "Within 60 days of shipping")
        }
        "Flipkart" -> List(rates.size) {
            TrackRow("Within 3-5 days of shipping", "Within 100 days of shipping")
        }
        else -> List(rates.size) {
            TrackRow("Within 3 days of shipping", "Within 60 days of shipping")
        }
    }

    val instructions = listOf(
        Triple("🛒", "START with\nan empty cart", "1"),
        Triple("🏷️", "Use Reward Club\nspecific coupon codes\nonly", "2"),
        Triple("📝", "COMPLETE\ntransaction in\none session", "3")
    )

    val quickTips = listOf(
        "Always use your OWN account registered with the merchant and NEVER share your Card details with anyone. Sharing of Card details will disqualify all transactions tagged to your card during the month.",
        "If a Product or Category is excluded from coins earning, Reward Club credits 1 Coin for that transaction, to acknowledge the same. Please refer detailed T&Cs",
        "The right to validate your transaction remains with the respective online Shopping Partner you transact on, and their validation is final",
        "Coins will be credited in the active state based on Partner validation and recognition of your transaction as payable",
        "Any dispute / complaints raised beyond 20 Days from the date of the transaction will not be entertained",
        "Do not use any Coupon code/offer which is not listed/shared on Reward Club. Use Reward Club's coupon code only for the specific brands if shared / hosted by Reward Club",
        "* All Rewards are in the form of Reward Club Coins *"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Earn Coins", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        },
        bottomBar = {
            // Proceed button
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
                elevation = CardDefaults.cardElevation(16.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = "🔒",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Secure redirection to $brandName (Coins tracking active)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkGreen
                        )
                    }
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val cuelinksApi = remember { com.rewardclub.app.api.CuelinksApiService() }
                    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

                    Button(
                        onClick = {
                            val rawUrl = when (brandName) {
                                "Amazon" -> "https://www.amazon.in"
                                "Flipkart" -> "https://www.flipkart.com"
                                "Myntra" -> "https://www.myntra.com"
                                "HP Pay" -> "https://www.hppay.in"
                                "Car Insurance" -> "https://www.policybazaar.com/motor-insurance/car-insurance/"
                                "Health Insurance" -> "https://www.policybazaar.com/health-insurance/"
                                "Term Life Insurance" -> "https://www.policybazaar.com/life-insurance/term-insurance/"
                                "HDFC ERGO Insurance" -> "https://www.hdfcergo.com"
                                "Personal Loan" -> "https://www.bankbazaar.com/personal-loan.html"
                                "Home Loan" -> "https://www.bankbazaar.com/home-loan.html"
                                "Car Loan" -> "https://www.bankbazaar.com/car-loan.html"
                                "Business Loan" -> "https://www.bankbazaar.com/business-loan.html"
                                else -> "https://www.flipkart.com"
                            }
                            // Generate the Cuelinks tracking URL
                            val trackingUrl = cuelinksApi.createAffiliateLink(rawUrl)

                            // 🔥 Background ping to register click even if Chrome blocks redirect
                            coroutineScope.launch {
                                cuelinksApi.fireAndForgetClick(rawUrl)
                            }

                            // 🚀 Open Cuelinks tracking URL in Chrome
                            // Chrome follows redirect: linksredirect.com → merchant site
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
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ACTIVATE COINS & SHOP",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GrayBackground)
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Brand detail header card - redesigned for premium visual pop
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Logo Header
                    val logoRes = when (brandName) {
                        "Amazon" -> R.drawable.amazon_logo
                        "Flipkart" -> R.drawable.flipkart_logo
                        "HP Pay" -> R.drawable.hp_pay_logo
                        "Myntra" -> R.drawable.myntra_logo
                        "Car Insurance" -> R.drawable.car_insurance_icon
                        "Health Insurance" -> R.drawable.health_insurance_icon
                        "Term Life Insurance" -> R.drawable.term_life_insurance_icon
                        "HDFC ERGO Insurance" -> R.drawable.hdfc_ergo_icon
                        "Personal Loan" -> R.drawable.personal_loan_icon
                        "Home Loan" -> R.drawable.home_loan_icon
                        "Car Loan" -> R.drawable.car_loan_icon
                        "Business Loan" -> R.drawable.business_loan_icon
                        "SBI SimplyCLICK Card", "SBI Card" -> R.drawable.sbi_logo
                        "HDFC Regalia Gold", "HDFC Bank" -> R.drawable.hdfc_logo
                        "ICICI Amazon Pay", "ICICI Bank" -> R.drawable.icici_logo
                        else -> null
                    }

                    val rewardSubtitle = when (brandName) {
                        "Amazon" -> "Earn Upto 12 Coins per ₹100 on qualifying Amazon orders"
                        "Flipkart" -> "Earn Upto 200 Coins per transaction on Flipkart orders"
                        "Myntra" -> "Earn Upto 10 Coins per ₹100 on Myntra lifestyle orders"
                        "HP Pay" -> "Earn Upto 10 Coins per ₹100 on HP Pay digital fuel payments"
                        "Car Insurance" -> "Earn Upto 2,000 Coins on verified policy issuance"
                        "Health Insurance" -> "Earn Upto 5,000 Coins on verified policy issuance"
                        "Term Life Insurance" -> "Earn Upto 8,000 Coins on verified policy issuance"
                        "HDFC ERGO Insurance" -> "Earn Upto 6,000 Coins on verified policy issuance"
                        "Personal Loan" -> "Earn Upto 10,000 Coins on verified loan disbursement"
                        "Home Loan" -> "Earn Upto 25,000 Coins on verified loan disbursement"
                        "Car Loan" -> "Earn Upto 15,000 Coins on verified loan disbursement"
                        "Business Loan" -> "Earn Upto 30,000 Coins on verified loan disbursement"
                        else -> "Earn Coins on your successful transactions via Reward Club"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFFF7F9FB), shape = RoundedCornerShape(12.dp))
                                .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoRes != null) {
                                Image(
                                    painter = painterResource(id = logoRes),
                                    contentDescription = brandName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = brandName.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = DarkGreen
                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = brandName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextDark
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Active Tracking",
                                        color = DarkGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF3E0), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, AmazonOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🪙", fontSize = 20.sp)
                        Text(
                            text = rewardSubtitle,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            lineHeight = 16.sp
                        )
                    }

                    HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))

                    Text(
                        text = "To ensure successful coin tracking, please check guide below:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )

                    // 3 steps cards with numbers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        instructions.forEach { (iconText, desc, index) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FBF9)),
                                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(130.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(LightGreen, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(iconText, fontSize = 16.sp)
                                    }
                                    Text(
                                        text = desc,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        color = TextDark,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Coin Table title
            Text(
                text = "Earning Rates & Confirmation Details",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = TextDark,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Dynamic Coin Table tab selectors
            var showTrackingTab by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(23.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (!showTrackingTab) DarkGreen else Color.Transparent,
                            shape = RoundedCornerShape(19.dp)
                        )
                        .clickable { showTrackingTab = false },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Earn Rates",
                        color = if (!showTrackingTab) White else TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (showTrackingTab) DarkGreen else Color.Transparent,
                            shape = RoundedCornerShape(19.dp)
                        )
                        .clickable { showTrackingTab = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tracking & Confirmation",
                        color = if (showTrackingTab) White else TextDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Redesigned premium cards rows (No legacy database tables)
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!showTrackingTab) {
                    rates.forEachIndexed { index, row ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(DarkGreen, shape = CircleShape)
                                    )
                                    Text(
                                        text = row.category,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextDark
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = row.earnRate,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = DarkGreen
                                    )
                                }
                            }
                        }
                    }
                } else {
                    trackings.forEachIndexed { index, row ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = White),
                            border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "⏱️ Coins Tracking",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextGray
                                    )
                                    Text(
                                        text = row.tracking,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextDark
                                    )
                                }
                                HorizontalDivider(color = BorderColor.copy(alpha = 0.3f))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✔️ Coins Confirmation",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextGray
                                    )
                                    Text(
                                        text = row.confirmation,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = DarkGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Tips Card - redesigned with warning alerts
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.6f)),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💡", fontSize = 18.sp)
                        Text(
                            text = "Quick Tips & Guidelines",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = TextDark
                        )
                    }

                    HorizontalDivider(color = BorderColor.copy(alpha = 0.5f))

                    quickTips.forEachIndexed { index, tip ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color(0xFFFFF3E0), shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    fontSize = 10.sp,
                                    color = GoldHex,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = tip,
                                fontSize = 12.sp,
                                color = TextGray,
                                lineHeight = 16.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
