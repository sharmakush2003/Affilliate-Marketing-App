package com.example.xyz.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.xyz.ui.theme.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.shadow
import kotlinx.coroutines.delay
import com.example.xyz.R

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
    val logoResId: Int? = null
)

data class InsuranceMock(
    val name: String,
    val logoText: String,
    val offerText: String,
    val subText: String,
    val logoBg: Color = Color(0xFFE8F5E9),
    val logoResId: Int? = null
)

data class LoanMock(
    val name: String,
    val logoText: String,
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
    onHamburgerClick: () -> Unit,
    onBrandClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllCouponsClick: () -> Unit,
    onCouponClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val brands = listOf(
        BrandEarn("Amazon", "amazon", "Upto 12", "per ₹100", Color(0xFFFF9900), R.drawable.amazon_logo),
        BrandEarn("Flipkart", "Flipkart", "Upto 200", "per transaction", Color(0xFF2874F0), R.drawable.flipkart_logo),
        BrandEarn("Myntra", "Myntra", "Upto 10", "per ₹100", Color(0xFFE63956), R.drawable.myntra_logo),
        BrandEarn("HP Pay", "HP Pay", "Upto 10", "per ₹100", Color(0xFF0033A0))
    )

    val categories = listOf(
        RedeemCategory("Products", "📦"),
        RedeemCategory("Vouchers", "🎟️"),
        RedeemCategory("Utilities", "🧾", true, "NEW")
    )

    val creditCards = listOf(
        CreditCardMock("SBI SimplyCLICK Card", "SBI Card", "5,000 Coins", "On card approval", Color(0xFF0F3E5F)),
        CreditCardMock("HDFC Regalia Gold", "HDFC Bank", "8,000 Coins", "On card approval", Color(0xFF1A1A1A)),
        CreditCardMock("ICICI Amazon Pay", "ICICI Bank", "3,500 Coins", "On card approval", Color(0xFFB85D06))
    )

    val insurances = listOf(
        InsuranceMock("Car Insurance", "🚗", "Upto 2,000 Coins", "Get instant policy online", Color(0xFFE3F2FD)),
        InsuranceMock("Health Insurance", "🛡️", "Upto 5,000 Coins", "Cashless claims network", Color(0xFFE8F5E9)),
        InsuranceMock("Term Life Insurance", "👥", "Upto 8,000 Coins", "Secure your family's future", Color(0xFFFFF3E0))
    )

    val loans = listOf(
        LoanMock("Personal Loan", "💰", "Upto 10,000 Coins", "Get instant loan approvals", Color(0xFFF1F8E9)),
        LoanMock("Home Loan", "🏠", "Upto 25,000 Coins", "Lowest interest rates on loans", Color(0xFFE8F5E9)),
        LoanMock("Car Loan", "🚗", "Upto 15,000 Coins", "Quick processing & payouts", Color(0xFFE3F2FD)),
        LoanMock("Business Loan", "📈", "Upto 30,000 Coins", "Fund your business growth", Color(0xFFFFF3E0))
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
        HeaderSection(onHamburgerClick = onHamburgerClick)

        // Coin Balance (Google Pay style, replacing Login card)
        CoinBalanceSection(onRedeemClick = { onCategoryClick("Products") })

        // Banner Slider
        BannerSliderSection()

        // Shop & Earn Coins (placed at the very top)
        SectionHeader(title = "Shop & Earn Coins", onViewAllClick = { onBrandClick("Amazon") })
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(brands) { brand ->
                BrandCard(brand = brand, onClick = { onBrandClick(brand.name) })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loan Enquiries [NEW]
        SectionHeader(title = "Loan Enquiries", showViewAll = false)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(loans) { loan ->
                LoanItem(loan = loan, onClick = { onBrandClick("Flipkart") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Insurance [NEW]
        SectionHeader(title = "Insurance", showViewAll = false)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(insurances) { insurance ->
                InsuranceItem(insurance = insurance, onClick = { onBrandClick("Flipkart") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Credit Cards [NEW]
        SectionHeader(title = "Credit Cards", showViewAll = false)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(creditCards) { card ->
                CreditCardItem(card = card, onClick = { onBrandClick("Flipkart") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zillion-Style Slogans Footer
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "that makes you go",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "ahaaaaaaa!",
                    color = DarkGreen,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Everyday 1 Lakh+ Users Earn & Spend Coins",
                    color = TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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
fun HeaderSection(onHamburgerClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .padding(horizontal = 8.dp, vertical = 8.dp) // adjusted for IconButton touch targets
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Hamburger icon
                IconButton(onClick = onHamburgerClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = White
                    )
                }

                // Reward Club Logo
                Image(
                    painter = painterResource(id = R.drawable.reward_club_logo),
                    contentDescription = "Reward Club Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = "Reward Club",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "JOIN",
                    color = White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { }
                )

                // Gold Hexagon/Badge "R+"
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFE5A93C), Color(0xFFF1D169))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = "R+",
                        color = Color(0xFF5B3C00),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun CoinBalanceSection(onRedeemClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onRedeemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gold Coin Circle
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(AccentGold, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 14.sp
                    )
                }
                
                Column {
                    Text(
                        text = "Reward Club Coins",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "2,450 Coins",
                        color = TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Claim History",
                    color = DarkGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BannerSliderSection() {
    val banners = listOf(
        R.drawable.banner_welcome,
        R.drawable.banner_amazon,
        R.drawable.banner_flipkart,
        R.drawable.banner_finance
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
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            Crossfade(
                targetState = currentSlide,
                animationSpec = tween(durationMillis = 600)
            ) { slideIndex ->
                Image(
                    painter = painterResource(id = banners[slideIndex]),
                    contentDescription = "Offer Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            banners.forEachIndexed { index, _ ->
                val size by animateDpAsState(
                    targetValue = if (index == currentSlide) 14.dp else 6.dp,
                    label = "dotWidth"
                )
                Box(
                    modifier = Modifier
                        .size(height = 6.dp, width = size)
                        .clip(CircleShape)
                        .background(
                            if (index == currentSlide) DarkGreen else Color.LightGray
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
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = TextDark,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            if (hasNewBadge) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(OrangeDiscount)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "New",
                        color = White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showViewAll) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onViewAllClick() }
            ) {
                Text(
                    text = "VIEW ALL",
                    color = DarkGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = DarkGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun BrandCard(brand: BrandEarn, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(brand.logoBg),
                contentAlignment = Alignment.Center
            ) {
                if (brand.logoResId != null) {
                    Image(
                        painter = painterResource(id = brand.logoResId),
                        contentDescription = brand.name,
                        modifier = Modifier.fillMaxHeight().padding(4.dp),
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

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = brand.earnRate,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                // Diamond coin shape
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AccentGold, shape = RoundedCornerShape(2.dp))
                )
            }
            Text(
                text = brand.rateDetail,
                color = TextGray,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
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
    Box(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
    ) {
        if (category.hasBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 6.dp, end = 6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(AccentGold)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
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
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = category.iconText,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.title,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
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
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .width(150.dp)
            .height(160.dp)
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
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
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
                        fontSize = 20.sp
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
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun VoucherCard(voucher: VoucherMock, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (voucher.discount.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AccentGold)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = voucher.discount,
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            if (voucher.logoResId != null) {
                Image(
                    painter = painterResource(id = voucher.logoResId),
                    contentDescription = voucher.name,
                    modifier = Modifier.size(36.dp).align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = voucher.name,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = voucher.subText,
                color = TextGray,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun CreditCardItem(card: CreditCardMock, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // A mini credit card visual design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                        Text(
                            text = card.logoText,
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "💳",
                            color = White,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = "•••• ••••",
                        color = White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diamond coin shape
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AccentGold, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = card.offerText,
                    color = DarkGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = card.subText,
                color = TextGray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun InsuranceItem(insurance: InsuranceMock, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(insurance.logoBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = insurance.logoText,
                    fontSize = 28.sp
                )
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diamond coin shape
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AccentGold, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = insurance.offerText,
                    color = DarkGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = insurance.subText,
                color = TextGray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TopProductCard(product: TopProductMock, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GrayBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.emoji,
                    fontSize = 36.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = product.name,
                color = TextDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Diamond coin shape
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AccentGold, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = product.cost,
                    color = DarkGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LoanItem(loan: LoanMock, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(loan.logoBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loan.logoText,
                    fontSize = 28.sp
                )
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(AccentGold, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = loan.offerText,
                    color = DarkGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = loan.subText,
                color = TextGray,
                fontSize = 10.sp
            )
        }
    }
}
