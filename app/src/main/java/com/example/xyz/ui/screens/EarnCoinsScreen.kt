package com.example.xyz.ui.screens

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
import com.example.xyz.R
import com.example.xyz.ui.theme.*
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
                colors = CardDefaults.cardColors(containerColor = White),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You will divert to third party website $brandName",
                        fontSize = 11.sp,
                        color = TextGray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val cuelinksApi = remember { com.example.xyz.api.CuelinksApiService() }
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
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "PROCEED TO EARN COINS",
                                fontSize = 15.sp,
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
            // Brand detail header card
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Logo Header
                    val logoRes = when (brandName) {
                        "Amazon" -> R.drawable.amazon_logo
                        "Flipkart" -> R.drawable.flipkart_logo
                        "HP Pay" -> R.drawable.hp_pay_logo
                        "Myntra" -> R.drawable.myntra_logo
                        else -> null
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(36.dp)
                                .background(Color(0xFFF0F4F8), shape = RoundedCornerShape(6.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoRes != null) {
                                Image(
                                    painter = painterResource(id = logoRes),
                                    contentDescription = brandName,
                                    modifier = Modifier.fillMaxHeight(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = brandName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Diamond Coin Icon
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AccentGold, shape = RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("RC", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Text(
                            text = if (brandName == "Amazon") {
                                "Get Upto 12 Coins per Rs.100 on your purchase from Amazon via Reward Club"
                            } else {
                                "Get Upto 200 Coins per transaction on your purchase from Flipkart via Reward Club"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Divider(color = BorderColor)

                    Text(
                        text = "Kindly Note: To earn Coins on your $brandName transaction, do ensure as depicted below",
                        fontSize = 12.sp,
                        color = TextGray
                    )

                    // 3 Steps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        instructions.forEach { (iconText, desc, index) ->
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(Color(0xFFE8F5E9), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(iconText, fontSize = 18.sp)
                                }
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center,
                                    color = TextDark,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Coin Table title
            Text(
                text = "When will you see your coins in your account?",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDark
            )

            // Dynamic Coin Table tab selectors
            var showTrackingTab by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(Color(0xFFEFEFEF), shape = RoundedCornerShape(22.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(
                            if (!showTrackingTab) DarkGreen else Color.Transparent,
                            shape = RoundedCornerShape(18.dp)
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
                            shape = RoundedCornerShape(18.dp)
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

            // Custom table layout
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F7FA))
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (!showTrackingTab) "Category" else "Coins Tracking",
                            color = TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = if (!showTrackingTab) "Earn Rate" else "Coins Confirmation",
                            color = TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(color = BorderColor)

                    // Table rows
                    if (!showTrackingTab) {
                        rates.forEachIndexed { index, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) White else Color(0xFFF9F9F9))
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    text = row.category,
                                    color = TextDark,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = row.earnRate,
                                    color = TextDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                        }
                    } else {
                        trackings.forEachIndexed { index, row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (index % 2 == 0) White else Color(0xFFF9F9F9))
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            ) {
                                Text(
                                    text = row.tracking,
                                    color = TextDark,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = row.confirmation,
                                    color = TextDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                            Divider(color = BorderColor.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            // Quick Tips Card
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Quick Tips",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextDark
                    )

                    quickTips.forEachIndexed { index, tip ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 12.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tip,
                                fontSize = 12.sp,
                                color = TextDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
