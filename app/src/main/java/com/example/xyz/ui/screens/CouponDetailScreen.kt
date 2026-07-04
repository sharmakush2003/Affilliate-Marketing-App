package com.example.xyz.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.xyz.R
import com.example.xyz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouponDetailScreen(
    couponName: String,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Normalize coupon name
    val normalizedName = when {
        couponName.contains("Bombay", ignoreCase = true) -> "Bombay Shaving Company"
        couponName.contains("Assembly", ignoreCase = true) -> "Assembly Travel"
        couponName.contains("Bloom", ignoreCase = true) -> "Bloom by Bold Care"
        couponName.contains("Deyga", ignoreCase = true) -> "Deyga"
        else -> couponName
    }

    val emoji = when (normalizedName) {
        "Bombay Shaving Company" -> "🪒"
        "Assembly Travel" -> "🧳"
        "Bloom by Bold Care" -> "🌸"
        "Deyga" -> "🌿"
        else -> "🎟️"
    }

    val terms = when (normalizedName) {
        "Bombay Shaving Company" -> listOf(
            "Add any 5 products to your cart from the given collection.",
            "Buy 5 Products at ₹875.",
            "BSC: grooming products for men, from shaving kits to skincare, and more."
        )
        "Assembly Travel" -> listOf(
            "Flat 5% Off on All-Time Classic Travel Luggage & Accessories.",
            "Offer is only applicable on the Landing Page. This offer cannot be clubbed with any other offer.",
            "Offer is only applicable on the Landing Page."
        )
        "Bloom by Bold Care" -> listOf(
            "Flat 15% Off on premium wellness products for men and women.",
            "Valid on minimum purchase of ₹499.",
            "Cannot be clubbed with other discount codes."
        )
        "Deyga" -> listOf(
            "Flat 10% Off on pure natural skincare and hair care products.",
            "Valid on minimum purchase of ₹599.",
            "Applicable on all products across the website."
        )
        else -> listOf(
            "Use coupon code on the merchant's checkout page.",
            "Coupon is valid for one-time use only.",
            "Cannot be combined with any other offers/promotions."
        )
    }

    val code = when (normalizedName) {
        "Bombay Shaving Company" -> "ZILBSC875"
        "Assembly Travel" -> "ZILASS5OFF"
        "Bloom by Bold Care" -> "ZILBLOOM15"
        "Deyga" -> "ZILDEYGA10"
        else -> "ZILXXXXXXXXXX"
    }

    var termsExpanded by remember { mutableStateOf(true) }
    var breakUpExpanded by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coupon Details", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
                    Button(
                        onClick = {
                            Toast.makeText(context, "Proceeding with $normalizedName coupon!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            text = "CONTINUE",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
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
            // Available Coins Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Coins",
                    fontSize = 14.sp,
                    color = TextGray,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(AccentGold, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🪙", fontSize = 10.sp)
                    }
                    Text(
                        text = "2,450",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }
            }

            // Main Coupon Brand Card
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val logoRes = when (normalizedName) {
                        "Bombay Shaving Company" -> R.drawable.bombay_shaving_logo
                        "Assembly Travel" -> R.drawable.assembly_travel_logo
                        "Bloom by Bold Care" -> R.drawable.bloom_logo
                        "Deyga" -> R.drawable.deyga_logo
                        else -> null
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (logoRes != null) {
                                Image(
                                    painter = painterResource(id = logoRes),
                                    contentDescription = normalizedName,
                                    modifier = Modifier.fillMaxSize().padding(8.dp),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(emoji, fontSize = 36.sp)
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = normalizedName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .background(AccentGold, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🪙", fontSize = 8.sp)
                                }
                                Text(
                                    text = "0",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreen
                                )
                            }
                        }
                    }

                    Divider(color = BorderColor)

                    // Collapsible Terms & Conditions Section
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { termsExpanded = !termsExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Terms & Conditions",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Icon(
                                imageVector = if (termsExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextDark
                            )
                        }

                        AnimatedVisibility(
                            visible = termsExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                terms.forEachIndexed { index, term ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${index + 1}:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDark
                                        )
                                        Text(
                                            text = term,
                                            fontSize = 12.sp,
                                            color = TextDark,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = BorderColor)

                    // Exchange rate
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFC), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(AccentGold, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🪙", fontSize = 8.sp)
                            }
                            Text(
                                text = "XYZ Exchange:",
                                fontSize = 12.sp,
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "4 Coins = 1 Rupee",
                            fontSize = 12.sp,
                            color = TextDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Coupon Code Block
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
                        text = "Coupon Code",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                            .border(1.dp, DarkGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(code))
                                Toast.makeText(context, "Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = code,
                            color = DarkGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy code",
                            tint = DarkGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Payment section
            Card(
                colors = CardDefaults.cardColors(containerColor = White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { breakUpExpanded = !breakUpExpanded }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payment",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Icon(
                            imageVector = if (breakUpExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextDark
                        )
                    }

                    AnimatedVisibility(
                        visible = breakUpExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Divider(color = BorderColor.copy(alpha = 0.5f))

                            Text(
                                text = "Charge break up",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Coupon Value", fontSize = 12.sp, color = TextGray)
                                Text("0 coins", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pay in coins", fontSize = 12.sp, color = TextGray)
                                Text("0 coins", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Pay in cash", fontSize = 12.sp, color = TextGray)
                                Text("₹0", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                            }

                            Divider(color = BorderColor.copy(alpha = 0.5f))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Grand Total", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .background(AccentGold, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("🪙", fontSize = 8.sp)
                                    }
                                    Text("0", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
