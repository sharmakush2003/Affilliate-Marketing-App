// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsuranceScreen(
    onBackClick: () -> Unit,
    onInsuranceClick: (String) -> Unit
) {
    val allInsurances = listOf(
        InsuranceMock("Vehicle Insurance", "🚗/🏍️", "Up to 25% Earning", "Instant car & bike policies online", Color(0xFFE3F2FD), R.drawable.car_insurance_icon),
        InsuranceMock("Health Insurance", "🛡️", "Up to 25% Earning", "Cashless claims network", Color(0xFFE8F5E9), R.drawable.health_insurance_icon),
        InsuranceMock("Term Life Insurance", "👥", "Up to 25% Earning", "Secure family's future", Color(0xFFFFF3E0), R.drawable.term_life_insurance_icon),
        InsuranceMock("HDFC ERGO Insurance", "🏥", "Up to 25% Earning", "Complete motor & health", Color(0xFFFFEBEE), R.drawable.hdfc_ergo_icon)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Insurance Partners", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
        ) {
            // Premium Info Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFFEF9E7), Color(0xFFE8F8F5))),
                        RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color(0xFFFADBD8), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Secure Your Assets & Health 🛡️",
                        color = Color(0xFF78281F),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Compare zero-commission policies online and get credited with thousands of Coins directly to your account.",
                        color = Color(0xFF2E4053),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                items(allInsurances) { insurance ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = White),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onInsuranceClick(insurance.name) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Icon Box
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(insurance.logoBg)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (insurance.logoResId != null) {
                                    Image(
                                        painter = painterResource(id = insurance.logoResId),
                                        contentDescription = insurance.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Text(text = insurance.logoText, fontSize = 24.sp)
                                }
                            }

                            // Details Column
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = insurance.name,
                                    color = TextDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                Text(
                                    text = insurance.subText,
                                    color = TextGray,
                                    fontSize = 12.sp
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(text = "💸", fontSize = 12.sp)
                                    Text(
                                        text = insurance.offerText,
                                        color = DarkGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            // Quote Button
                            Button(
                                onClick = { onInsuranceClick(insurance.name) },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "Quote 🛡️",
                                    color = White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
