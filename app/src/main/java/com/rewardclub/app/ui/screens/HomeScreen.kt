// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Mic
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rewardclub.app.ui.theme.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.delay
import com.rewardclub.app.R
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.rewardclub.app.data.CampaignData
import com.rewardclub.app.data.CampaignItem
import com.rewardclub.app.data.CategoryGroup

// Mock data classes
data class BrandEarn(
    val name: String,
    val logoText: String,
    val earnRate: String,
    val rateDetail: String,
    val logoBg: Color = Color(0xFFF0F4F8),
    val logoResId: Int? = null
)

data class RedeemCategory(
    val title: String,
    val iconText: String,
    val hasBadge: Boolean = false,
    val badgeText: String = ""
)

data class CouponMock(
    val title: String,
    val discount: String = ""
)

data class VoucherMock(
    val name: String,
    val subText: String,
    val discount: String = "",
    val logoResId: Int? = null
)

data class CreditCardMock(
    val name: String,
    val logoText: String,
    val offerText: String,
    val subText: String,
    val logoBg: Color = Color(0xFF1E1E1E),
    val logoResId: Int? = null,
    val description: String = ""
)

data class InsuranceMock(
    val name: String,
    val logoText: String = "",
    val offerText: String,
    val subText: String,
    val logoBg: Color = Color(0xFFE8F5E9),
    val logoResId: Int? = null
)

data class LoanMock(
    val name: String,
    val logoText: String = "",
    val offerText: String,
    val subText: String,
    val logoBg: Color = Color(0xFFECEFF1),
    val logoResId: Int? = null
)

data class TopProductMock(
    val name: String,
    val cost: String,
    val emoji: String
)

@Composable
fun HomeScreen(
    scrollState: ScrollState,
    onHamburgerClick: () -> Unit,
    onJoinClick: () -> Unit = {},
    onBrandClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllCouponsClick: () -> Unit,
    onCouponClick: (String) -> Unit,
    onViewAllBrandsClick: () -> Unit,
    onViewAllCardsClick: () -> Unit,
    onViewAllPersonalLoansClick: () -> Unit,
    onViewAllCarLoansClick: () -> Unit,
    onViewAllTwoWheelerLoansClick: () -> Unit,
    onViewAllInsuranceClick: () -> Unit
) {
    val categories = listOf(
        RedeemCategory("Products", "📦"),
        RedeemCategory("Vouchers", "🎟️"),
        RedeemCategory("Utilities", "🧾", true, "NEW")
    )

    val coupons = listOf(
        CouponMock("Bloom by Bold Care", "15% Discount"),
        CouponMock("Bombay Shaving Company", "0% Discount"),
        CouponMock("Assembly Travel", "0% Discount"),
        CouponMock("Deyga", "10% Discount")
    )

    val vouchers = listOf(
        VoucherMock("Amazon Pay gift card", "Amazon", "2% OFF", R.drawable.amazon_logo),
        VoucherMock("Amazon shopping voucher", "Amazon Shopping..", "2.25% OFF", R.drawable.amazon_logo),
        VoucherMock("Flipkart gift card", "Flipkart", "3% OFF", R.drawable.flipkart_logo),
        VoucherMock("HP Pay voucher", "HPCL", "1.5% OFF")
    )

    val topProducts = listOf(
        TopProductMock("Usha EI 2801 LT Electric Dry Iron", "3,798 Coins", "🔌"),
        TopProductMock("High Speed Hand Mixer with 7 Speed", "9,321 Coins", "🥣"),
        TopProductMock("Element 1010 Stainless Steel Bottle", "2,144 Coins", "🧴"),
        TopProductMock("Philips HR1855/70 Viva Juicer", "51,232 Coins", "🍹")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .verticalScroll(scrollState)
    ) {
        // Green Header
        HeaderSection(onHamburgerClick = onHamburgerClick, onJoinClick = onJoinClick)

        // Banner Slider Section
        BannerSliderSection()
        Spacer(modifier = Modifier.height(6.dp))

        // Official Partner Categories (Shop, Personal Loans, Car Loans, 2-Wheeler Loans, Insurance, Credit Cards)
        CampaignData.allCategories.forEach { category ->
            SectionHeader(
                title = "${category.iconEmoji} ${category.title}",
                showViewAll = true,
                onViewAllClick = {
                    when (category.id) {
                        "shop" -> onViewAllBrandsClick()
                        "personal_loans", "loans" -> onViewAllPersonalLoansClick()
                        "car_loans" -> onViewAllCarLoansClick()
                        "two_wheeler_loans" -> onViewAllTwoWheelerLoansClick()
                        "insurance" -> onViewAllInsuranceClick()
                        "credit_cards" -> onViewAllCardsClick()
                        else -> onCategoryClick(category.title)
                    }
                }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(category.campaigns) { campaign ->
                    CampaignCard(campaign = campaign, onClick = { onBrandClick(campaign.name) })
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun XYZIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f)
            lineTo(size.width, size.height / 3f)
            lineTo(size.width / 2f, size.height)
            lineTo(0f, size.height / 3f)
            close()
        }
        drawPath(path = path, color = color)
        
        // Internal stripes (fan style)
        val stripeWidth = size.width / 10f
        drawRect(
            color = DarkGreen,
            topLeft = androidx.compose.ui.geometry.Offset(size.width/2f - stripeWidth/2f, size.height/4f),
            size = androidx.compose.ui.geometry.Size(stripeWidth, size.height/2f)
        )
    }
}

@Composable
fun HeaderSection(onHamburgerClick: () -> Unit, onJoinClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyDark)
            .statusBarsPadding()
            .border(BorderStroke(0.5.dp, BorderColor.copy(alpha = 0.5f)))
            .padding(bottom = 12.dp)
    ) {
        // ── Top row: hamburger | logo + name | JOIN ──────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onHamburgerClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = TextDark)
                }
                Image(
                    painter = painterResource(id = R.drawable.reward_club_logo),
                    contentDescription = "Reward Club Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Reward Club",
                    color = TextDark,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.2.sp
                )
            }

            // Premium Join pill
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFFFF9900), Color(0xFFFFB74D))
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onJoinClick() }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "Join Free",
                    color = Color(0xFF0F1111),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun CoinBalanceSection(onRedeemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onRedeemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(all = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFFFD54F), Color(0xFFFF8F00))
                                ),
                                shape = CircleShape
                            )
                            .border(1.5.dp, BorderColor.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪙", fontSize = 24.sp)
                    }
                    Column {
                        Text(
                            text = "Reward Club Coins",
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        val isLoggedIn = com.rewardclub.app.utils.UserSession.currentUser != null
                        Text(
                            text = if (isLoggedIn) "${com.rewardclub.app.utils.UserSession.totalCoins} Coins" else "Join to Earn",
                            color = TextDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                
                // Redeem Pill Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .background(Color(0xFFF0F5FA), RoundedCornerShape(20.dp))
                        .border(1.dp, Color(0xFF0066CC).copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(text = "Redeem", color = Color(0xFF0066CC), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Redeem arrow",
                        tint = Color(0xFF0066CC),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BannerSliderSection() {
    val banners = listOf(
        R.drawable.banner_credit_cards,
        R.drawable.banner_electronics_tech,
        R.drawable.banner_fashion_beauty,
        R.drawable.banner_travel_flights
    )
    
    var currentSlide by remember { mutableStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            currentSlide = (currentSlide + 1) % banners.size
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .shadow(2.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
        ) {
            Crossfade(
                targetState = currentSlide,
                animationSpec = tween(durationMillis = 600)
            ) { slideIndex ->
                Image(
                    painter = painterResource(id = banners[slideIndex]),
                    contentDescription = "Offer Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        
        Spacer(modifier = Modifier.height(6.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.forEachIndexed { index, _ ->
                val width by animateDpAsState(
                    targetValue = if (index == currentSlide) 14.dp else 5.dp,
                    label = "dotWidth"
                )
                Box(
                    modifier = Modifier
                        .size(height = 4.dp, width = width)
                        .clip(CircleShape)
                        .background(
                            if (index == currentSlide)
                                Brush.horizontalGradient(listOf(Color(0xFFE8A020), Color(0xFF006B3F)))
                            else Brush.horizontalGradient(listOf(Color(0xFFDDDDDD), Color(0xFFDDDDDD)))
                        )
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    showViewAll: Boolean = true,
    hasNewBadge: Boolean = false,
    onViewAllClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Subtle left accent bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFFE8A020), DarkGreen)),
                        RoundedCornerShape(1.5.dp)
                    )
            )
            Text(
                text = title,
                color = TextDark,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.1.sp
            )
            if (hasNewBadge) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(OrangeDiscount)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "New",
                        color = White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showViewAll) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onViewAllClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "View all",
                    color = DarkGreen,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
fun CampaignCard(campaign: CampaignItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.6.dp, BorderColor.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(0.5.dp),
        modifier = Modifier
            .width(108.dp)
            .height(138.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(0.5.dp, BorderColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (campaign.logoResId != null) {
                    Image(
                        painter = painterResource(id = campaign.logoResId),
                        contentDescription = campaign.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(campaign.logoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = campaign.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Campaign Name & Subtitle
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = campaign.name,
                    color = TextDark,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = campaign.subtitle,
                    color = TextGray,
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }

            // Earning Pill & Detail
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(text = "🪙", fontSize = 8.sp)
                        Text(
                            text = campaign.earnCoinsText,
                            color = DarkGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = campaign.rateDetail,
                    color = TextGray,
                    fontSize = 7.5.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BrandCard(brand: BrandEarn, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (brand.logoResId != null) {
                    Image(
                        painter = painterResource(id = brand.logoResId),
                        contentDescription = brand.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = brand.logoText,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Premium tag badge for rate
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 10.sp)
                    Text(
                        text = "${brand.earnRate} Coins",
                        color = DarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = brand.rateDetail,
                color = TextGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CategoryGridItem(
    category: RedeemCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.hasBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AccentGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = category.badgeText,
                        color = Color.Black,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.iconText,
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = category.title,
                    color = TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CouponCard(coupon: CouponMock, onClick: () -> Unit) {
    val logoRes = when (coupon.title) {
        "Bloom by Bold Care" -> R.drawable.bloom_logo
        "Bombay Shaving Company" -> R.drawable.bombay_shaving_logo
        "Assembly Travel" -> R.drawable.assembly_travel_logo
        "Deyga" -> R.drawable.deyga_logo
        else -> null
    }

    val emoji = when (coupon.title) {
        "Bloom by Bold Care" -> "🌸"
        "Bombay Shaving Company" -> "🪒"
        "Assembly Travel" -> "🧳"
        "Deyga" -> "🌿"
        else -> "🎟️"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .height(170.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (logoRes != null) {
                    Image(
                        painter = painterResource(id = logoRes),
                        contentDescription = coupon.title,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = emoji,
                        fontSize = 28.sp
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = coupon.title,
                    color = TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = coupon.discount,
                    color = OrangeDiscount,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun VoucherCard(voucher: VoucherMock, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (voucher.logoResId != null) {
                    Image(
                        painter = painterResource(id = voucher.logoResId),
                        contentDescription = voucher.name,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text("🎟️", fontSize = 28.sp)
                }

                if (voucher.discount.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(AccentGold)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = voucher.discount,
                            color = Color.Black,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = voucher.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = voucher.subText,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CreditCardItem(card: CreditCardMock, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // A mini credit card visual design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(card.logoBg)
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (card.logoResId != null) {
                            Image(
                                painter = painterResource(id = card.logoResId),
                                contentDescription = card.logoText,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Text(
                                text = card.logoText,
                                color = White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "💳",
                            color = White,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "•••• ••••",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = card.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(LightGreen, shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 10.sp)
                    Text(
                        text = card.offerText,
                        color = DarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = card.subText,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun InsuranceItem(insurance: InsuranceMock, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (insurance.logoResId != null) {
                    Image(
                        painter = painterResource(id = insurance.logoResId),
                        contentDescription = insurance.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = insurance.logoText,
                        fontSize = 28.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = insurance.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "💸", fontSize = 10.sp)
                    Text(
                        text = insurance.offerText,
                        color = DarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = insurance.subText,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TopProductCard(product: TopProductMock, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.emoji,
                    fontSize = 32.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = product.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFFFECEF), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 10.sp)
                    Text(
                        text = product.cost,
                        color = OrangeDiscount,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun LoanItem(loan: LoanMock, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFAFAFA))
                    .border(1.dp, BorderColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (loan.logoResId != null) {
                    Image(
                        painter = painterResource(id = loan.logoResId),
                        contentDescription = loan.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = loan.logoText,
                        fontSize = 28.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = loan.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🪙", fontSize = 10.sp)
                    Text(
                        text = loan.offerText,
                        color = DarkGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = loan.subText,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



