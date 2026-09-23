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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rewardclub.app.data.CampaignData
import com.rewardclub.app.data.CampaignItem
import com.rewardclub.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarLoansScreen(
    onBackClick: () -> Unit,
    onLoanClick: (String) -> Unit
) {
    val newCarLoans = remember { CampaignData.newCarLoans }
    val usedCarLoans = remember { CampaignData.usedCarLoans }
    val allCarLoans = remember { CampaignData.carLoansCategory.campaigns }

    var selectedFilter by remember { mutableStateOf("All") }

    val filteredPartners = remember(selectedFilter, allCarLoans, newCarLoans, usedCarLoans) {
        when (selectedFilter) {
            "New Car" -> newCarLoans
            "Used Car" -> usedCarLoans
            else -> allCarLoans
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Car Loan Partners", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
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
            // Car Loan Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFFE3F2FD), Color(0xFFE8EAF6))),
                        RoundedCornerShape(16.dp)
                    )
                    .border(1.dp, Color(0xFFBBDEFB), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = "Fast API",
                            tint = Color(0xFF1565C0),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Car Loan — New & Used • Up to 90% Funding",
                            color = Color(0xFF0D47A1),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Pan-India coverage, minimal documentation, flexible tenure and attractive interest rates for new & pre-owned vehicles.",
                        color = Color(0xFF2E4053),
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Quick Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "New Car", "Used Car").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) DarkGreen else Color.White)
                            .border(1.dp, if (isSelected) DarkGreen else BorderColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else TextDark,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Partner Cards Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp)
            ) {
                items(filteredPartners, key = { it.id }) { partner ->
                    CarLoanCard(partner = partner, onClick = { onLoanClick(partner.name) })
                }
            }
        }
    }
}

@Composable
fun CarLoanCard(partner: CampaignItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Partner Logo
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (partner.logoResId != null) {
                    Image(
                        painter = painterResource(id = partner.logoResId),
                        contentDescription = partner.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(partner.logoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = partner.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Partner details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = partner.name,
                        color = TextDark,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    partner.badge?.let { badgeText ->
                        Box(
                            modifier = Modifier
                                .background(partner.badgeBgColor, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = partner.badgeTextColor,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Text(
                    text = partner.subtitle,
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(text = "🪙", fontSize = 11.sp)
                    Text(
                        text = "${partner.earnCoinsText} Coins on Disbursal",
                        color = DarkGreen,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Apply CTA Button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp).defaultMinSize(minWidth = 68.dp)
            ) {
                Text(
                    text = "Apply",
                    color = White,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
