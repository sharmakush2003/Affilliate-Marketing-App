// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.data

import androidx.compose.ui.graphics.Color
import com.rewardclub.app.R
import com.rewardclub.app.ui.theme.DarkGreen

data class CampaignRateRow(
    val category: String,
    val rate: String
)

data class CampaignItem(
    val id: Int,
    val name: String,
    val subtitle: String,
    val earnCoinsText: String,
    val rateDetail: String,
    val logoUrl: String,
    val targetUrl: String,
    val categoryName: String,
    val badge: String? = null,
    val badgeBgColor: Color = Color(0xFFE8F5E9),
    val badgeTextColor: Color = DarkGreen,
    val cookieDuration: String = "30 Days",
    val payoutRate: String = "",
    val trackingWindow: String = "Within 24-48 hours",
    val approvalWindow: String = "Within 30-45 days",
    val rates: List<CampaignRateRow> = emptyList(),
    val instructions: List<String> = emptyList(),
    val logoResId: Int? = null
)

data class CategoryGroup(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val description: String,
    val gradientColors: List<Color>,
    val campaigns: List<CampaignItem>
)

object CampaignData {

    // ── 1. SHOP (Top Retail & E-commerce Brands) ──
    val shoppingCategory = CategoryGroup(
        id = "shop",
        title = "Shop",
        iconEmoji = "🛍️",
        description = "Top mega-retailers & e-commerce stores across India",
        gradientColors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)),
        campaigns = listOf(
            CampaignItem(
                id = 1001,
                name = "Amazon",
                subtitle = "Mobiles, Fashion, Electronics & More",
                earnCoinsText = "UPTO 6%",
                rateDetail = "Up to 6% Cashback on qualifying orders",
                logoUrl = "https://www.google.com/s2/favicons?domain=amazon.in&sz=128",
                targetUrl = "https://www.amazon.in",
                categoryName = "Shop",
                badge = "POPULAR",
                badgeBgColor = Color(0xFFFFF3E0),
                badgeTextColor = Color(0xFFE65100),
                payoutRate = "Up to 6.0%",
                rates = listOf(
                    CampaignRateRow("Fashion & Apparel", "60 Coins per ₹100 (6%)"),
                    CampaignRateRow("Home & Kitchen", "50 Coins per ₹100 (5%)"),
                    CampaignRateRow("Electronics & Mobiles", "20 Coins per ₹100 (2%)")
                ),
                instructions = listOf(
                    "Click Activate to visit Amazon official store.",
                    "Add items to cart and complete checkout.",
                    "Coins tracked within 24-48 hours of shipment."
                ),
                logoResId = R.drawable.amazon_logo
            ),
            CampaignItem(
                id = 1002,
                name = "Flipkart",
                subtitle = "Electronics, Fashion & Everyday Essentials",
                earnCoinsText = "UPTO 7%",
                rateDetail = "Up to 7% Cashback on popular products",
                logoUrl = "https://www.google.com/s2/favicons?domain=flipkart.com&sz=128",
                targetUrl = "https://www.flipkart.com",
                categoryName = "Shop",
                badge = "HOT DEAL",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF1565C0),
                payoutRate = "Up to 7.0%",
                rates = listOf(
                    CampaignRateRow("Fashion & Footwear", "70 Coins per ₹100 (7%)"),
                    CampaignRateRow("Books & General", "50 Coins per ₹100 (5%)"),
                    CampaignRateRow("Electronics", "30 Coins per ₹100 (3%)")
                ),
                instructions = listOf(
                    "Redirect to Flipkart via Reward Club.",
                    "Do not use external promo codes.",
                    "Coins credited after return window closes."
                ),
                logoResId = R.drawable.flipkart_logo
            ),
            CampaignItem(
                id = 1003,
                name = "Myntra",
                subtitle = "India's biggest online fashion store",
                earnCoinsText = "UPTO 6%",
                rateDetail = "Up to 6% Cashback on trending fashion",
                logoUrl = "https://www.google.com/s2/favicons?domain=myntra.com&sz=128",
                targetUrl = "https://www.myntra.com",
                categoryName = "Shop",
                badge = "TRENDING",
                badgeBgColor = Color(0xFFFCE4EC),
                badgeTextColor = Color(0xFFC2185B),
                payoutRate = "Up to 6.0%",
                rates = listOf(
                    CampaignRateRow("Apparel & Western Wear", "60 Coins per ₹100 (6%)"),
                    CampaignRateRow("Footwear & Accessories", "50 Coins per ₹100 (5%)")
                ),
                instructions = listOf(
                    "Start with an empty cart on Myntra.",
                    "Place order immediately after clicking.",
                    "Instant session logging."
                ),
                logoResId = R.drawable.myntra_logo
            ),
            CampaignItem(
                id = 1004,
                name = "Meesho",
                subtitle = "Affordable fashion, beauty & home supplies",
                earnCoinsText = "UPTO 5%",
                rateDetail = "Up to 5% Cashback on all catalogue orders",
                logoUrl = "https://www.google.com/s2/favicons?domain=meesho.com&sz=128",
                targetUrl = "https://www.meesho.com",
                categoryName = "Shop",
                badge = "SAVINGS",
                badgeBgColor = Color(0xFFF3E5F5),
                badgeTextColor = Color(0xFF7B1FA2),
                payoutRate = "Up to 5.0%",
                rates = listOf(
                    CampaignRateRow("All Meesho Collections", "50 Coins per ₹100 (5%)")
                )
            ),
            CampaignItem(
                id = 1005,
                name = "Nykaa",
                subtitle = "100% genuine cosmetics, skincare & perfumes",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Cashback on beauty orders",
                logoUrl = "https://www.google.com/s2/favicons?domain=nykaa.com&sz=128",
                targetUrl = "https://www.nykaa.com",
                categoryName = "Shop",
                badge = "GENUINE",
                badgeBgColor = Color(0xFFFCE4EC),
                badgeTextColor = Color(0xFFAD1457),
                payoutRate = "Up to 4.0%",
                rates = listOf(
                    CampaignRateRow("Cosmetics & Makeup", "40 Coins per ₹100 (4%)"),
                    CampaignRateRow("Skincare & Haircare", "40 Coins per ₹100 (4%)")
                )
            ),
            CampaignItem(
                id = 1006,
                name = "Croma",
                subtitle = "Gadgets, smartphones, laptops & appliances",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Cashback on consumer tech",
                logoUrl = "https://www.google.com/s2/favicons?domain=croma.com&sz=128",
                targetUrl = "https://www.croma.com",
                categoryName = "Shop",
                badge = "ELECTRONICS",
                badgeBgColor = Color(0xFFE0F2F1),
                badgeTextColor = Color(0xFF00796B),
                payoutRate = "Up to 4.0%",
                rates = listOf(
                    CampaignRateRow("Electronics & Appliances", "40 Coins per ₹100 (4%)")
                )
            ),
            CampaignItem(
                id = 1007,
                name = "Reliance Digital",
                subtitle = "Smart TVs, audio systems, laptops & accessories",
                earnCoinsText = "UPTO 3%",
                rateDetail = "Up to 3% Cashback on electronics",
                logoUrl = "https://www.google.com/s2/favicons?domain=reliancedigital.in&sz=128",
                targetUrl = "https://www.reliancedigital.in",
                categoryName = "Shop",
                badge = "TECH DEALS",
                badgeBgColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                payoutRate = "Up to 3.0%",
                rates = listOf(
                    CampaignRateRow("Tech & Digital Gadgets", "30 Coins per ₹100 (3%)")
                )
            ),
            CampaignItem(
                id = 1008,
                name = "Ajio",
                subtitle = "Trendy fashion, footwear & accessories",
                earnCoinsText = "UPTO 7%",
                rateDetail = "Up to 7% Cashback on latest fashion collections",
                logoUrl = "https://www.google.com/s2/favicons?domain=ajio.com&sz=128",
                targetUrl = "https://www.ajio.com",
                categoryName = "Shop",
                badge = "BESTSELLER",
                badgeBgColor = Color(0xFFEDE7F6),
                badgeTextColor = Color(0xFF512DA8),
                payoutRate = "Up to 7.0%",
                rates = listOf(
                    CampaignRateRow("All Fashion Categories", "70 Coins per ₹100 (7%)")
                )
            ),
            CampaignItem(
                id = 1009,
                name = "JioMart",
                subtitle = "Groceries, daily essentials, staples & kitchenware",
                earnCoinsText = "UPTO 5%",
                rateDetail = "Up to 5% Cashback on groceries & staples",
                logoUrl = "https://www.google.com/s2/favicons?domain=jiomart.com&sz=128",
                targetUrl = "https://www.jiomart.com",
                categoryName = "Shop",
                badge = "GROCERY",
                badgeBgColor = Color(0xFFE8F5E9),
                badgeTextColor = Color(0xFF2E7D32),
                payoutRate = "Up to 5.0%",
                rates = listOf(
                    CampaignRateRow("Groceries & Daily Essentials", "50 Coins per ₹100 (5%)")
                )
            ),
            CampaignItem(
                id = 1010,
                name = "Lenskart",
                subtitle = "Eyeglasses, sunglasses, computer glasses & frames",
                earnCoinsText = "UPTO 7%",
                rateDetail = "Up to 7% Cashback on eyewear & sunglasses",
                logoUrl = "https://www.google.com/s2/favicons?domain=lenskart.com&sz=128",
                targetUrl = "https://www.lenskart.com",
                categoryName = "Shop",
                badge = "EYEWEAR",
                badgeBgColor = Color(0xFFE0F7FA),
                badgeTextColor = Color(0xFF00838F),
                payoutRate = "Up to 7.0%",
                rates = listOf(
                    CampaignRateRow("Eyeglasses & Frames", "70 Coins per ₹100 (7%)"),
                    CampaignRateRow("Sunglasses", "70 Coins per ₹100 (7%)")
                )
            )
        )
    )

    // ── 2. PERSONAL LOAN (16 Partners — Powered by Unified Financial APIs) ──
    val personalLoansCategory = CategoryGroup(
        id = "personal_loans",
        title = "Personal Loan",
        iconEmoji = "💰",
        description = "Verified digital personal loans & instant disbursals from top NBFCs & Banks",
        gradientColors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9)),
        campaigns = listOf(
            CampaignItem(
                id = 4001,
                name = "HDFC Personal Loan",
                subtitle = "Instant loan approvals up to ₹40 Lakhs",
                earnCoinsText = "UPTO 1.5%",
                rateDetail = "Up to 1.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=hdfcbank.com&sz=128",
                targetUrl = "https://www.hdfcbank.com/personal/borrow/popular-loans/personal-loan",
                categoryName = "Loan",
                badge = "LOW INTEREST",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF0D47A1),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 1.5% Disbursal",
                trackingWindow = "Instant Webhook",
                approvalWindow = "Within 48 hours",
                rates = listOf(
                    CampaignRateRow("Loan Disbursed", "150 Coins per ₹10,000 (1.5%)")
                ),
                instructions = listOf(
                    "Check eligibility instantly with Aadhaar & PAN.",
                    "Complete online digital KYC verification.",
                    "Coins credited immediately on bank disbursal."
                ),
                logoResId = R.drawable.hdfc_logo
            ),
            CampaignItem(
                id = 4002,
                name = "BharatPe Personal Loan",
                subtitle = "Hassle-free collateral-free credit for personal & business needs",
                earnCoinsText = "UPTO 2%",
                rateDetail = "Up to 2% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=bharatpe.com&sz=128",
                targetUrl = "https://bharatpe.com/loans",
                categoryName = "Loan",
                badge = "QUICK APPROVAL",
                badgeBgColor = Color(0xFFE0F2F1),
                badgeTextColor = Color(0xFF00695C),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Credit Disbursal", "200 Coins per ₹10,000 (2.0%)")
                )
            ),
            CampaignItem(
                id = 4003,
                name = "IDFC FIRST Bank Personal Loan",
                subtitle = "100% paperless digital loan with flexible EMIs",
                earnCoinsText = "UPTO 2%",
                rateDetail = "Up to 2% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=idfcfirstbank.com&sz=128",
                targetUrl = "https://www.idfcfirstbank.com/personal-banking/loans/personal-loan",
                categoryName = "Loan",
                badge = "PAPERLESS",
                badgeBgColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Disbursed Loan", "200 Coins per ₹10,000 (2.0%)")
                )
            ),
            CampaignItem(
                id = 4004,
                name = "Poonawalla Fincorp",
                subtitle = "Affordable interest rates & rapid paperless processing",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=poonawallafincorp.com&sz=128",
                targetUrl = "https://poonawallafincorp.com/personal-loan",
                categoryName = "Loan",
                badge = "INSTANT DISBURSAL",
                badgeBgColor = Color(0xFFEDE7F6),
                badgeTextColor = Color(0xFF4527A0),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Successful Disbursal", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4005,
                name = "Moneyview Personal Loan",
                subtitle = "Digital personal loans up to ₹10 Lakhs in minutes",
                earnCoinsText = "UPTO 3%",
                rateDetail = "Up to 3% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=moneyview.in&sz=128",
                targetUrl = "https://moneyview.in/personal-loans",
                categoryName = "Loan",
                badge = "TOP RATED",
                badgeBgColor = Color(0xFFE8F5E9),
                badgeTextColor = Color(0xFF2E7D32),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 3.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Disbursed Amount", "300 Coins per ₹10,000 (3.0%)")
                )
            ),
            CampaignItem(
                id = 4006,
                name = "KreditBee Personal Loan",
                subtitle = "Fast digital KYC & 15-minute fund transfers to bank",
                earnCoinsText = "UPTO 3%",
                rateDetail = "Up to 3% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=kreditbee.in&sz=128",
                targetUrl = "https://www.kreditbee.in/personal-loan",
                categoryName = "Loan",
                badge = "FAST KYC",
                badgeBgColor = Color(0xFFFFF8E1),
                badgeTextColor = Color(0xFFF57F17),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 3.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Instant Disbursal", "300 Coins per ₹10,000 (3.0%)")
                )
            ),
            CampaignItem(
                id = 4007,
                name = "DMI Finance",
                subtitle = "Technology-driven digital credit line & personal loans",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=dmifinance.in&sz=128",
                targetUrl = "https://www.dmifinance.in/",
                categoryName = "Loan",
                badge = "DIGITAL LENDING",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF1565C0),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Credit Facility Approval", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4008,
                name = "Hero Instant Personal Loan",
                subtitle = "Hero FinCorp instant funds for salaried & self-employed",
                earnCoinsText = "UPTO 3%",
                rateDetail = "Up to 3% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=herofincorp.com&sz=128",
                targetUrl = "https://www.herofincorp.com/instant-personal-loans",
                categoryName = "Loan",
                badge = "HERO FINCORP",
                badgeBgColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 3.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Disbursed Loan", "300 Coins per ₹10,000 (3.0%)")
                )
            ),
            CampaignItem(
                id = 4009,
                name = "Prefr Personal Loan",
                subtitle = "Quick pre-approved personal loans with zero physical paperwork",
                earnCoinsText = "UPTO 3%",
                rateDetail = "Up to 3% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=prefr.com&sz=128",
                targetUrl = "https://prefr.com/",
                categoryName = "Loan",
                badge = "PRE-APPROVED",
                badgeBgColor = Color(0xFFEDE7F6),
                badgeTextColor = Color(0xFF512DA8),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 3.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Disbursed Loan", "300 Coins per ₹10,000 (3.0%)")
                )
            ),
            CampaignItem(
                id = 4010,
                name = "Zapcash Personal Loan",
                subtitle = "Rapid short-term credit & emergency funds",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=zapcash.in&sz=128",
                targetUrl = "https://zapcash.in/",
                categoryName = "Loan",
                badge = "HIGH PAYOUT",
                badgeBgColor = Color(0xFFE0F2F1),
                badgeTextColor = Color(0xFF00796B),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Loan Disbursal", "400 Coins per ₹10,000 (4.0%)")
                )
            ),
            CampaignItem(
                id = 4011,
                name = "Ring Personal Loan",
                subtitle = "Flexible digital power loan line with zero prepayment charges",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=ring.pe&sz=128",
                targetUrl = "https://ring.pe/",
                categoryName = "Loan",
                badge = "ZERO FORECLOSURE",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF0277BD),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Loan Amount Disbursal", "400 Coins per ₹10,000 (4.0%)")
                )
            ),
            CampaignItem(
                id = 4012,
                name = "ASAP Finance Personal Loan",
                subtitle = "Fast online loans with convenient monthly repayment schedules",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=asapfinance.in&sz=128",
                targetUrl = "https://asapfinance.in/",
                categoryName = "Loan",
                badge = "QUICK CASH",
                badgeBgColor = Color(0xFFF3E5F5),
                badgeTextColor = Color(0xFF6A1B9A),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Approved & Disbursed", "400 Coins per ₹10,000 (4.0%)")
                )
            ),
            CampaignItem(
                id = 4013,
                name = "Payme Personal Loan",
                subtitle = "Instant cash loans in 15 minutes for salaried professionals",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=paymeindia.in&sz=128",
                targetUrl = "https://www.paymeindia.in/",
                categoryName = "Loan",
                badge = "SALARIED LOAN",
                badgeBgColor = Color(0xFFFFF3E0),
                badgeTextColor = Color(0xFFD84315),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Loan Disbursed to Bank", "400 Coins per ₹10,000 (4.0%)")
                )
            ),
            CampaignItem(
                id = 4014,
                name = "Insta Money Personal Loan",
                subtitle = "Instant micro personal loans up to ₹50,000 for emergency needs",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=instamoney.app&sz=128",
                targetUrl = "https://www.instamoney.app/",
                categoryName = "Loan",
                badge = "MINIMAL DOCS",
                badgeBgColor = Color(0xFFECEFF1),
                badgeTextColor = Color(0xFF37474F),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Disbursal Amount", "400 Coins per ₹10,000 (4.0%)")
                )
            ),
            CampaignItem(
                id = 4015,
                name = "MPokket Personal Loan",
                subtitle = "Instant cash loans for college students & salaried employees",
                earnCoinsText = "UPTO 5%",
                rateDetail = "Up to 5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=mpokket.in&sz=128",
                targetUrl = "https://www.mpokket.in/",
                categoryName = "Loan",
                badge = "STUDENTS & SALARIED",
                badgeBgColor = Color(0xFFE8F5E9),
                badgeTextColor = Color(0xFF1B5E20),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 5.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Approved Cash Disbursal", "500 Coins per ₹10,000 (5.0%)")
                )
            ),
            CampaignItem(
                id = 4016,
                name = "FatakPay Personal Loan",
                subtitle = "Instant salary advance & financial backing with minimal verification",
                earnCoinsText = "UPTO 4%",
                rateDetail = "Up to 4% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=fatakpay.com&sz=128",
                targetUrl = "https://fatakpay.com/",
                categoryName = "Loan",
                badge = "SALARY ADVANCE",
                badgeBgColor = Color(0xFFFFF8E1),
                badgeTextColor = Color(0xFFF57F17),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 4.0% Disbursal",
                rates = listOf(
                    CampaignRateRow("Loan Disbursed", "400 Coins per ₹10,000 (4.0%)")
                )
            )
        )
    )

    // ── 2A. CAR LOANS (New & Used Car Loan Partners) ──
    val newCarLoans = listOf(
        CampaignItem(
            id = 4101,
            name = "Axis Bank Car Loan",
            subtitle = "Up to 90% On-Road Funding • Flexible Tenure",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=axisbank.com&sz=128",
            targetUrl = "https://www.axisbank.com/retail/loans/car-loan",
            categoryName = "Car Loan",
            badge = "NEW CAR",
            badgeBgColor = Color(0xFFE3F2FD),
            badgeTextColor = Color(0xFF0D47A1),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Car Loan Disbursed", "200 Coins per ₹10,000 (2.0%)")
            ),
            instructions = listOf(
                "Apply online for instant in-principle car loan sanction.",
                "Choose car model and dealership.",
                "Disbursal tracked directly with coin rewards."
            )
        ),
        CampaignItem(
            id = 4102,
            name = "Cholamandalam Car Loan",
            subtitle = "Quick Processing • Flexible EMI Tenure • Easy Approval",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=cholamandalam.com&sz=128",
            targetUrl = "https://www.cholamandalam.com/vehicle-finance/car-loan",
            categoryName = "Car Loan",
            badge = "CHOLA FINANCE",
            badgeBgColor = Color(0xFFFFF3E0),
            badgeTextColor = Color(0xFFE65100),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("New Car Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        ),
        CampaignItem(
            id = 4103,
            name = "HDB Financial Car Loan",
            subtitle = "Fast approvals & minimal documentation for new cars",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=hdbfs.com&sz=128",
            targetUrl = "https://www.hdbfs.com/products/auto-loans/new-car-loan",
            categoryName = "Car Loan",
            badge = "HDB SERVICES",
            badgeBgColor = Color(0xFFE8F5E9),
            badgeTextColor = Color(0xFF2E7D32),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Car Loan Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        ),
        CampaignItem(
            id = 4104,
            name = "YES Bank Car Loan",
            subtitle = "Attractive interest rates & up to 100% on-road funding",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=yesbank.in&sz=128",
            targetUrl = "https://www.yesbank.in/personal-banking/loans/car-loan",
            categoryName = "Car Loan",
            badge = "YES BANK",
            badgeBgColor = Color(0xFFEDE7F6),
            badgeTextColor = Color(0xFF512DA8),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Car Loan Disbursed", "200 Coins per ₹10,000 (2.0%)")
            )
        )
    )

    val usedCarLoans = listOf(
        CampaignItem(
            id = 4105,
            name = "Axis Bank Used Car Loan",
            subtitle = "Pan-India verification • Low interest • Fast disbursals",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=axisbank.com&sz=128",
            targetUrl = "https://www.axisbank.com/retail/loans/used-car-loan",
            categoryName = "Car Loan",
            badge = "USED CAR",
            badgeBgColor = Color(0xFFE3F2FD),
            badgeTextColor = Color(0xFF0D47A1),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Used Car Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        ),
        CampaignItem(
            id = 4106,
            name = "HDB Financial Used Car Loan",
            subtitle = "Hassle-free pre-owned car financing with flexible EMIs",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=hdbfs.com&sz=128",
            targetUrl = "https://www.hdbfs.com/products/auto-loans/used-car-loan",
            categoryName = "Car Loan",
            badge = "HDB SERVICES",
            badgeBgColor = Color(0xFFE8F5E9),
            badgeTextColor = Color(0xFF2E7D32),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Used Car Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        ),
        CampaignItem(
            id = 4107,
            name = "IDFC FIRST Bank Used Car Loan",
            subtitle = "100% digital KYC • Transparent rates • Pan-India",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=idfcfirstbank.com&sz=128",
            targetUrl = "https://www.idfcfirstbank.com/personal-banking/loans/pre-owned-car-loan",
            categoryName = "Car Loan",
            badge = "IDFC FIRST",
            badgeBgColor = Color(0xFFFFEBEE),
            badgeTextColor = Color(0xFFC62828),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Used Car Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        ),
        CampaignItem(
            id = 4108,
            name = "TVS Credit Used Car Loan",
            subtitle = "Quick valuation, minimal documentation & attractive interest",
            earnCoinsText = "UPTO 2%",
            rateDetail = "Up to 2% Disbursal Payout in Coins",
            logoUrl = "https://www.google.com/s2/favicons?domain=tvscredit.com&sz=128",
            targetUrl = "https://www.tvscredit.com/used-car-loan",
            categoryName = "Car Loan",
            badge = "TVS CREDIT",
            badgeBgColor = Color(0xFFE0F2F1),
            badgeTextColor = Color(0xFF00695C),
            cookieDuration = "Direct Server API",
            payoutRate = "Up to 2.0% Disbursal",
            rates = listOf(
                CampaignRateRow("Used Car Disbursal", "200 Coins per ₹10,000 (2.0%)")
            )
        )
    )

    val carLoansCategory = CategoryGroup(
        id = "car_loans",
        title = "Car Loan",
        iconEmoji = "🚗",
        description = "New & Used Car Loans with low interest rates & up to 90% funding",
        gradientColors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)),
        campaigns = newCarLoans + usedCarLoans
    )

    // ── 2B. TWO WHEELER LOANS (Bike Loan Partners) ──
    val twoWheelerLoansCategory = CategoryGroup(
        id = "two_wheeler_loans",
        title = "Two Wheeler Loan",
        iconEmoji = "🏍️",
        description = "Instant approvals, flexible EMIs & 100% digital bike loans",
        gradientColors = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9)),
        campaigns = listOf(
            CampaignItem(
                id = 4201,
                name = "Hero FinCorp Two Wheeler Loan",
                subtitle = "Quick Processing • Instant Approval • 100% Digital Process",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=herofincorp.com&sz=128",
                targetUrl = "https://www.herofincorp.com/two-wheeler-loan",
                categoryName = "Two Wheeler Loan",
                badge = "QUICK APPROVAL",
                badgeBgColor = Color(0xFFE8F5E9),
                badgeTextColor = Color(0xFF2E7D32),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4202,
                name = "TVS Credit Two Wheeler Loan",
                subtitle = "Attractive Rates • Flexible Tenure • Instant Approval",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=tvscredit.com&sz=128",
                targetUrl = "https://www.tvscredit.com/two-wheeler-loan",
                categoryName = "Two Wheeler Loan",
                badge = "LOW INTEREST",
                badgeBgColor = Color(0xFFE0F2F1),
                badgeTextColor = Color(0xFF00695C),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4203,
                name = "Bajaj Finserv Two Wheeler Loan",
                subtitle = "Easy EMI Options • Instant Approval • Low Interest",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=bajajfinserv.in&sz=128",
                targetUrl = "https://www.bajajfinserv.in/two-wheeler-loan",
                categoryName = "Two Wheeler Loan",
                badge = "EASY EMI",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF0D47A1),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4204,
                name = "SHRIRAM Finance Two Wheeler Loan",
                subtitle = "Trusted & Reliable • Flexible Tenure • 100% Digital",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=shriramfinance.in&sz=128",
                targetUrl = "https://www.shriramfinance.in/two-wheeler-loan",
                categoryName = "Two Wheeler Loan",
                badge = "TRUSTED & RELIABLE",
                badgeBgColor = Color(0xFFFFF8E1),
                badgeTextColor = Color(0xFFF57F17),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4205,
                name = "Tata Capital Two Wheeler Loan",
                subtitle = "Minimal Documentation • Instant Approval • Fast Disbursal",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=tatacapital.com&sz=128",
                targetUrl = "https://www.tatacapital.com/two-wheeler-loan.html",
                categoryName = "Two Wheeler Loan",
                badge = "MINIMAL DOCS",
                badgeBgColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            ),
            CampaignItem(
                id = 4206,
                name = "HDB Financial Two Wheeler Loan",
                subtitle = "Fast Disbursal • Flexible Tenure • 100% Digital Process",
                earnCoinsText = "UPTO 2.5%",
                rateDetail = "Up to 2.5% Disbursal Payout in Coins",
                logoUrl = "https://www.google.com/s2/favicons?domain=hdbfs.com&sz=128",
                targetUrl = "https://www.hdbfs.com/products/auto-loans/two-wheeler-loan",
                categoryName = "Two Wheeler Loan",
                badge = "FAST DISBURSAL",
                badgeBgColor = Color(0xFFEDE7F6),
                badgeTextColor = Color(0xFF512DA8),
                cookieDuration = "Direct Server API",
                payoutRate = "Up to 2.5% Disbursal",
                rates = listOf(
                    CampaignRateRow("Bike Loan Disbursed", "250 Coins per ₹10,000 (2.5%)")
                )
            )
        )
    )

    // ── 3. INSURANCE (Health, Vehicle, Life & Assets) ──
    val insuranceCategory = CategoryGroup(
        id = "insurance",
        title = "Insurance",
        iconEmoji = "🛡️",
        description = "Comprehensive vehicle, health & life coverage with high coin earnings",
        gradientColors = listOf(Color(0xFFFEF9E7), Color(0xFFE8F8F5)),
        campaigns = listOf(
            CampaignItem(
                id = 5001,
                name = "Vehicle Insurance",
                subtitle = "Instant car & bike policies online with cashless repairs",
                earnCoinsText = "UPTO 25%",
                rateDetail = "Up to 25% Earning in Coins on policy purchase",
                logoUrl = "https://www.google.com/s2/favicons?domain=policybazaar.com&sz=128",
                targetUrl = "https://www.policybazaar.com/motor-insurance/",
                categoryName = "Insurance",
                badge = "INSTANT POLICY",
                badgeBgColor = Color(0xFFE3F2FD),
                badgeTextColor = Color(0xFF0D47A1),
                payoutRate = "Up to 25.0%",
                rates = listOf(
                    CampaignRateRow("Car & Two Wheeler Policy", "250 Coins per ₹1,000 (25%)")
                ),
                logoResId = R.drawable.car_insurance_icon
            ),
            CampaignItem(
                id = 5002,
                name = "Health Insurance",
                subtitle = "Cashless hospitalization network across 10,000+ hospitals",
                earnCoinsText = "UPTO 25%",
                rateDetail = "Up to 25% Earning in Coins on family health cover",
                logoUrl = "https://www.google.com/s2/favicons?domain=policybazaar.com&sz=128",
                targetUrl = "https://www.policybazaar.com/health-insurance/",
                categoryName = "Insurance",
                badge = "TAX BENEFIT",
                badgeBgColor = Color(0xFFE8F5E9),
                badgeTextColor = Color(0xFF1B5E20),
                payoutRate = "Up to 25.0%",
                rates = listOf(
                    CampaignRateRow("Family Health Policy", "250 Coins per ₹1,000 (25%)")
                ),
                logoResId = R.drawable.health_insurance_icon
            ),
            CampaignItem(
                id = 5003,
                name = "Term Life Insurance",
                subtitle = "Secure your family's future with ₹1 Crore cover from ₹450/month",
                earnCoinsText = "UPTO 25%",
                rateDetail = "Up to 25% Earning in Coins on term insurance",
                logoUrl = "https://www.google.com/s2/favicons?domain=policybazaar.com&sz=128",
                targetUrl = "https://www.policybazaar.com/life-insurance/term-insurance/",
                categoryName = "Insurance",
                badge = "1 CR COVER",
                badgeBgColor = Color(0xFFFFF3E0),
                badgeTextColor = Color(0xFFE65100),
                payoutRate = "Up to 25.0%",
                rates = listOf(
                    CampaignRateRow("Term Life Policy", "250 Coins per ₹1,000 (25%)")
                ),
                logoResId = R.drawable.term_life_insurance_icon
            ),
            CampaignItem(
                id = 5004,
                name = "HDFC ERGO Insurance",
                subtitle = "Trusted general insurance for motor, health & travel",
                earnCoinsText = "UPTO 25%",
                rateDetail = "Up to 25% Earning in Coins with instant claims",
                logoUrl = "https://www.google.com/s2/favicons?domain=hdfcergo.com&sz=128",
                targetUrl = "https://www.hdfcergo.com/",
                categoryName = "Insurance",
                badge = "TRUSTED BRAND",
                badgeBgColor = Color(0xFFFFEBEE),
                badgeTextColor = Color(0xFFC62828),
                payoutRate = "Up to 25.0%",
                rates = listOf(
                    CampaignRateRow("Motor & Health Cover", "250 Coins per ₹1,000 (25%)")
                ),
                logoResId = R.drawable.hdfc_ergo_icon
            )
        )
    )

    // ── 4. CREDIT CARD (Pre-Approved & High-Earning Cards) ──
    val creditCardCategory = CategoryGroup(
        id = "credit_cards",
        title = "Credit Card",
        iconEmoji = "💳",
        description = "High-earning verified credit cards & banking offers",
        gradientColors = listOf(Color(0xFFE8EAF6), Color(0xFFE3F2FD)),
        campaigns = listOf(
            CampaignItem(
                id = 4800,
                name = "AU Bank Credit Card",
                subtitle = "Pre-Approved Credit Card with lifetime rewards",
                earnCoinsText = "19,500 Coins",
                rateDetail = "₹1,950 payout on card approval",
                logoUrl = "https://www.google.com/s2/favicons?domain=aubank.in&sz=128",
                targetUrl = "https://cconboarding.au.bank.in/auccself/",
                categoryName = "Credit Card",
                badge = "POPULAR",
                cookieDuration = "30 Days",
                payoutRate = "₹1,950 / Lead",
                trackingWindow = "Within 24-48 hours",
                approvalWindow = "Within 45 days",
                rates = listOf(
                    CampaignRateRow("Approved Credit Card", "19,500 Coins (Flat ₹1,950)")
                ),
                instructions = listOf(
                    "Complete credit card application with Aadhaar & PAN.",
                    "Ensure mobile number matches Aadhaar records.",
                    "Coins credited after successful physical or digital KYC verification."
                )
            ),
            CampaignItem(
                id = 4615,
                name = "Axis Bank Credit Card",
                subtitle = "Cashback & Rewards Card with edge rewards",
                earnCoinsText = "18,900 Coins",
                rateDetail = "₹1,890 payout on card approval",
                logoUrl = "https://www.google.com/s2/favicons?domain=axisbank.com&sz=128",
                targetUrl = "https://web.axis.bank.in/DigitalChannel/WebForm/",
                categoryName = "Credit Card",
                badge = "BESTSELLER",
                cookieDuration = "30 Days",
                payoutRate = "₹1,890 / Lead",
                trackingWindow = "Within 24-48 hours",
                approvalWindow = "Within 45 days",
                rates = listOf(
                    CampaignRateRow("New Card Approval", "18,900 Coins (Flat ₹1,890)")
                ),
                instructions = listOf(
                    "Apply through the official Axis Bank onboarding flow.",
                    "Select card type (Flipkart Axis, Ace, or Neo).",
                    "Coins awarded after bank verifies employment and issues card."
                )
            ),
            CampaignItem(
                id = 4239,
                name = "SBI Elite Credit Card",
                subtitle = "Luxury & Lifestyle Card with airport lounge access",
                earnCoinsText = "18,900 Coins",
                rateDetail = "₹1,890 payout on card dispatch",
                logoUrl = "https://www.google.com/s2/favicons?domain=sbicard.com&sz=128",
                targetUrl = "https://www.sbicard.com/sprint/elite",
                categoryName = "Credit Card",
                badge = "HIGH PAYOUT",
                cookieDuration = "30 Days",
                payoutRate = "₹1,890 / Lead",
                trackingWindow = "Within 48 hours",
                approvalWindow = "Within 60 days",
                rates = listOf(
                    CampaignRateRow("Card Issuance", "18,900 Coins (Flat ₹1,890)")
                ),
                instructions = listOf(
                    "Submit application on official SBI Card portal.",
                    "Valid for new-to-bank credit card customers only."
                ),
                logoResId = R.drawable.sbi_logo
            ),
            CampaignItem(
                id = 5507,
                name = "HDFC Bank Swiggy Card",
                subtitle = "10% Food Cashback Card on Swiggy & dining",
                earnCoinsText = "18,290 Coins",
                rateDetail = "₹1,829 payout on card approval",
                logoUrl = "https://www.google.com/s2/favicons?domain=hdfcbank.com&sz=128",
                targetUrl = "https://applyonline.hdfc.bank.in/cards/credit-cards.html",
                categoryName = "Credit Card",
                badge = "EXCLUSIVE",
                cookieDuration = "30 Days",
                payoutRate = "₹1,829.25 / Lead",
                trackingWindow = "Within 24-48 hours",
                approvalWindow = "Within 45 days",
                rates = listOf(
                    CampaignRateRow("Card Disbursal", "18,290 Coins (Flat ₹1,829)")
                ),
                instructions = listOf(
                    "Apply online through HDFC Bank's official digital platform.",
                    "Card activation will trigger instant commission confirmation."
                ),
                logoResId = R.drawable.hdfc_logo
            ),
            CampaignItem(
                id = 4606,
                name = "IDFC First Bank Card",
                subtitle = "Never-Expiring Rewards & Zero Annual Fee",
                earnCoinsText = "9,750 Coins",
                rateDetail = "₹975 payout on approval",
                logoUrl = "https://www.google.com/s2/favicons?domain=idfcfirstbank.com&sz=128",
                targetUrl = "https://www.idfcfirstbank.com/credit-card/ntb-diy/apply",
                categoryName = "Credit Card",
                badge = "INSTANT KYC",
                cookieDuration = "30 Days",
                payoutRate = "₹975 / Lead",
                trackingWindow = "Within 24 hours",
                approvalWindow = "Within 45 days",
                rates = listOf(
                    CampaignRateRow("First Millennia / Classic Card", "9,750 Coins (Flat ₹975)")
                ),
                instructions = listOf(
                    "Fill online form and complete Aadhaar video-KYC."
                )
            )
        )
    )

    // Master list of all categories:
    val allCategories: List<CategoryGroup> = listOf(
        shoppingCategory,
        personalLoansCategory,
        carLoansCategory,
        twoWheelerLoansCategory,
        insuranceCategory,
        creditCardCategory
    )

    val allCampaigns: List<CampaignItem> = allCategories.flatMap { it.campaigns }

    fun findCampaign(name: String): CampaignItem? {
        val trimmed = name.trim().lowercase()
        return allCampaigns.find { it.name.lowercase() == trimmed }
            ?: allCampaigns.find { it.name.lowercase().contains(trimmed) || trimmed.contains(it.name.lowercase()) }
            ?: allCampaigns.find { it.targetUrl.lowercase().contains(trimmed) }
    }
}
