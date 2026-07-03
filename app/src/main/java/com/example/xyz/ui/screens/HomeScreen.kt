package com.example.xyz.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
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

@Composable
fun HomeScreen(
    onBrandClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onViewAllCouponsClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val brands = listOf(
        BrandEarn("Amazon", "amazon", "Upto 12", "per ₹100", Color(0xFFFF9900), R.drawable.amazon_logo),
        BrandEarn("Flipkart", "Flipkart", "Upto 200", "per transaction", Color(0xFF2874F0), R.drawable.flipkart_logo),
        BrandEarn("Myntra", "Myntra", "Upto 10", "per ₹100", Color(0xFFE63956), R.drawable.myntra_logo)
    )

    val categories = listOf(
        RedeemCategory("Products", "📦"),
        RedeemCategory("Vouchers", "🎟️"),
        RedeemCategory("Utilities", "🧾", true, "NEW")
    )

    val coupons = listOf(
        CouponMock("Bloom by Bold Care"),
        CouponMock("Bombay Shaving Company")
    )

    val vouchers = listOf(
        VoucherMock("Amazon Pay gift card", "Amazon", "2% OFF", R.drawable.amazon_logo),
        VoucherMock("Amazon shopping voucher", "Amazon Shopping..", "2.25% OFF", R.drawable.amazon_logo)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground)
            .verticalScroll(scrollState)
    ) {
        // Green Header
        HeaderSection()

        // Login Card
        LoginCardSection()

        // Banner Slider
        BannerSliderSection()

        // Shop & Earn Coins
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

        // Redeem coins on
        SectionHeader(title = "Redeem coins on", showViewAll = false)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                CategoryGridItem(
                    category = category,
                    modifier = Modifier.weight(1f),
                    onClick = { onCategoryClick(category.title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Coupons
        SectionHeader(
            title = "Coupons",
            hasNewBadge = true,
            onViewAllClick = onViewAllCouponsClick
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(coupons) { coupon ->
                CouponCard(coupon = coupon, onClick = onViewAllCouponsClick)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Popular Vouchers
        SectionHeader(title = "Popular Vouchers", onViewAllClick = { onCategoryClick("Vouchers") })
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(vouchers) { voucher ->
                VoucherCard(voucher = voucher, onClick = { onCategoryClick("Vouchers") })
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
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkGreen)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Diamond Leaf Icon
                XYZIcon(modifier = Modifier.size(24.dp))
                
                Text(
                    text = "XYZ",
                    color = White,
                    fontSize = 22.sp,
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

                // Gold Hexagon/Badge "X+"
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
                        text = "X+",
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
fun LoginCardSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1.3f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "New user?",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFFE8F5E9))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Log In?",
                        color = DarkGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = DarkGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BannerSliderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF7A1FA2), Color(0xFF3F51B5))
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Shop the Flipkart",
                color = White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "GOAT SALE",
                color = AccentGold,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Earn up to 200 Coins per transaction",
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = AccentGold),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(text = "Shop Now", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Carousel dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (0..5).forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) DarkGreen else White.copy(alpha = 0.5f))
                    )
                }
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
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .background(White)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Badge at top right if present
            if (category.hasBadge) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .offset(y = (-6).dp)
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

            Text(
                text = category.iconText,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = coupon.title.take(1),
                    fontSize = 24.sp,
                    color = DarkGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = coupon.title,
                color = TextDark,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
