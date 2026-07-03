package com.example.xyz.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Launch
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
import com.example.xyz.ui.theme.*

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

    val rates = listOf(
        RateRow("Apparel", "Earn 12 coins per Rs.100"),
        RateRow("Beauty, Luxury Beauty", "Earn 12 coins per Rs.100"),
        RateRow("Grocery", "Earn 6 coins per Rs.100"),
        RateRow("Luggage", "Earn 10 coins per Rs.100"),
        RateRow("Shoes, handbags & Accessories", "Earn 10 coins per Rs.100"),
        RateRow("Watches", "Earn 9 coins per Rs.100"),
        RateRow("Sports", "Earn 8 coins per Rs.100"),
        RateRow("Lawn and Garden", "Earn 8 coins per Rs.100"),
        RateRow("Personal care appliances", "Earn 10 coins per Rs.100"),
        RateRow("Books", "Earn 6 coins per Rs.100"),
        RateRow("Toys", "Earn 5 coins per Rs.100"),
        RateRow("Cell Phones & Accessories", "Earn upto 1 coin per Rs.100")
    )

    val trackings = List(12) {
        TrackRow("Within 3 days of Shipping", "Within 60 days of Shipping")
    }

    val instructions = listOf(
        Triple("🛒", "START with\nan empty cart", "1"),
        Triple("🏷️", "Use XYZ\nspecific coupon codes\nonly", "2"),
        Triple("📝", "COMPLETE\ntransaction in\none session", "3")
    )

    val quickTips = listOf(
        "Always use your OWN account registered with the merchant and NEVER share your XYZ Card details with anyone. Sharing of XYZ Card details will disqualify all transactions tagged to your card during the month.",
        "If a Product or Category is excluded from coins earning, XYZ credits 1 Coin for that transaction, to acknowledge the same. Please refer detailed T&Cs",
        "The right to validate your transaction remains with the respective online Shopping Partner you transact on, and their validation is final",
        "XYZ coins will be credited in the active state based on Partner validation and recognition of your transaction as payable",
        "Any dispute / complaints raised beyond 20 Days from the date of the transaction will not be entertained",
        "Do not use any Coupon code/offer which is not listed/shared on XYZ. Use XYZ's coupon code only for the specific brands if shared / hosted by XYZ",
        "* All Rewards are in the form of XYZ Coins *"
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
                    Button(
                        onClick = { },
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(90.dp)
                                .height(36.dp)
                                .background(Color(0xFFF0F4F8), shape = RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = brandName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
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
                            Text("XYZ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }

                        Text(
                            text = if (brandName == "Amazon") {
                                "Get Upto 12 XYZ Coins per Rs.100 on your purchase from Amazon via XYZ"
                            } else {
                                "Get Upto 200 XYZ Coins per transaction on your purchase from Flipkart via XYZ"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                    }

                    Divider(color = BorderColor)

                    Text(
                        text = "Kindly Note: To earn XYZ coins on your $brandName transaction, do ensure as depicted below",
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showTrackingTab = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showTrackingTab) DarkGreen else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Earn Rates")
                }
                Button(
                    onClick = { showTrackingTab = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showTrackingTab) DarkGreen else Color.LightGray
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tracking/Confirmation")
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
                            .background(DarkGreen)
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = if (!showTrackingTab) "Category" else "Coins Tracking",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = if (!showTrackingTab) "Earn Rate" else "Coins Confirmation",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }

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
