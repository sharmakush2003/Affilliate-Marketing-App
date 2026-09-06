// 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.data

import androidx.compose.ui.graphics.Color
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
    val trackingWindow: String = "Within 3 days of transaction",
    val approvalWindow: String = "Within 45-60 days",
    val rates: List<CampaignRateRow> = emptyList(),
    val instructions: List<String> = emptyList()
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

    // 1. Credit Cards & Finance
    val financeCategory = CategoryGroup(
        id = "finance",
        title = "Credit Card & Finance",
        iconEmoji = "💳",
        description = "High-earning verified credit cards & banking offers",
        gradientColors = listOf(Color(0xFFE8EAF6), Color(0xFFE3F2FD)),
        campaigns = listOf(
            CampaignItem(
                id = 4800,
                name = "AU Bank Credit Card",
                subtitle = "Pre-Approved Credit Card",
                earnCoinsText = "19,500 Coins",
                rateDetail = "₹1,950 payout on card approval",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4800/original/New_Project_-_2022-07-11T183540.715.png?1657544815",
                targetUrl = "https://cconboarding.au.bank.in/auccself/",
                categoryName = "Credit Card & Finance",
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
                subtitle = "Cashback & Rewards Card",
                earnCoinsText = "18,900 Coins",
                rateDetail = "₹1,890 payout on card approval",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4615/original/New_Project_-_2021-12-27T105943.715.png?1640583024",
                targetUrl = "https://web.axis.bank.in/DigitalChannel/WebForm/",
                categoryName = "Credit Card & Finance",
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
                subtitle = "Luxury & Lifestyle Card",
                earnCoinsText = "18,900 Coins",
                rateDetail = "₹1,890 payout on card dispatch",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4239/original/New_Project_-_2024-11-19T124149.520.png?1732000324",
                targetUrl = "https://www.sbicard.com/sprint/elite",
                categoryName = "Credit Card & Finance",
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
                )
            ),
            CampaignItem(
                id = 5507,
                name = "HDFC Bank Swiggy Card",
                subtitle = "10% Food Cashback Card",
                earnCoinsText = "18,290 Coins",
                rateDetail = "₹1,829 payout on card approval",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5507/original/New_Project_-_2023-09-08T175535.904.png?1694584444",
                targetUrl = "https://applyonline.hdfc.bank.in/cards/credit-cards.html",
                categoryName = "Credit Card & Finance",
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
                )
            ),
            CampaignItem(
                id = 5105,
                name = "SBI BPCL Fuel Card",
                subtitle = "Fuel Cashback & Rewards Card",
                earnCoinsText = "18,900 Coins",
                rateDetail = "₹1,890 payout on card approval",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5105/original/New_Project_%2891%29.png?1676031137",
                targetUrl = "https://www.sbicard.com/sprint/bpcl",
                categoryName = "Credit Card & Finance",
                badge = "FUEL SAVINGS",
                cookieDuration = "30 Days",
                payoutRate = "₹1,890 / Lead",
                trackingWindow = "Within 48 hours",
                approvalWindow = "Within 45 days",
                rates = listOf(
                    CampaignRateRow("Approved Fuel Card", "18,900 Coins (Flat ₹1,890)")
                ),
                instructions = listOf(
                    "Complete digital application for BPCL SBI Card.",
                    "Get up to 7.25% value back on BPCL fuel purchases."
                )
            ),
            CampaignItem(
                id = 4606,
                name = "IDFC First Bank Card",
                subtitle = "Never-Expiring Rewards",
                earnCoinsText = "9,750 Coins",
                rateDetail = "₹975 payout on approval",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4606/original/New_Project_-_2021-12-15T130721.045.png?1639555432",
                targetUrl = "https://www.idfcfirst.bank.in/credit-card/ntb-diy/apply",
                categoryName = "Credit Card & Finance",
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

    // 2. Electronics & Tech Brands
    val electronicsCategory = CategoryGroup(
        id = "electronics",
        title = "Electronics & Tech Brands",
        iconEmoji = "🎧",
        description = "Wearables, laptops, audio gear & smart gadgets",
        gradientColors = listOf(Color(0xFFE0F2F1), Color(0xFFE8F5E9)),
        campaigns = listOf(
            CampaignItem(
                id = 4232,
                name = "boAt Lifestyle",
                subtitle = "Earphones, Speakers & Smartwatches",
                earnCoinsText = "Upto 60 Coins per ₹100",
                rateDetail = "6% Cashback on audio & wearables",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4232/original/New_Project_%2851%29_%281%29.png?1603884221",
                targetUrl = "https://www.boat-lifestyle.com/",
                categoryName = "Electronics & Tech Brands",
                badge = "TOP BRAND",
                cookieDuration = "30 Days",
                payoutRate = "6.0% Per Sale",
                rates = listOf(
                    CampaignRateRow("Airdopes & True Wireless", "60 Coins per ₹100 (6%)"),
                    CampaignRateRow("Smartwatches & Bands", "60 Coins per ₹100 (6%)"),
                    CampaignRateRow("Speakers & Soundbars", "60 Coins per ₹100 (6%)")
                ),
                instructions = listOf(
                    "Add items to cart after redirecting from Reward Club.",
                    "Do not apply non-Reward Club coupon codes at checkout."
                )
            ),
            CampaignItem(
                id = 3278,
                name = "Noise (GoNoise)",
                subtitle = "Smart wearables & wireless audio",
                earnCoinsText = "Upto 42 Coins per ₹100",
                rateDetail = "4.2% Cashback on smartwatches",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/3278/original/NoiseLogo-01_%281%29.png?1713530556",
                targetUrl = "https://www.gonoise.com/",
                categoryName = "Electronics & Tech Brands",
                badge = "SMART TECH",
                cookieDuration = "30 Days",
                payoutRate = "4.2% Per Sale",
                rates = listOf(
                    CampaignRateRow("Smartwatches", "42 Coins per ₹100 (4.2%)"),
                    CampaignRateRow("Wireless Earbuds", "42 Coins per ₹100 (4.2%)"),
                    CampaignRateRow("Accessories", "42 Coins per ₹100 (4.2%)")
                )
            ),
            CampaignItem(
                id = 4360,
                name = "Acer India",
                subtitle = "Laptops, Desktops & Displays",
                earnCoinsText = "Upto 26 Coins per ₹100",
                rateDetail = "2.62% Cashback on computing gear",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4360/original/logo20250811-880075-1u1ka4n.jpg?1754903398",
                targetUrl = "https://store.acer.com/en-in/",
                categoryName = "Electronics & Tech Brands",
                badge = "LAPTOPS",
                cookieDuration = "30 Days",
                payoutRate = "2.625% Per Sale",
                rates = listOf(
                    CampaignRateRow("Predator & Nitro Gaming", "26 Coins per ₹100"),
                    CampaignRateRow("Swift & Aspire Laptops", "26 Coins per ₹100"),
                    CampaignRateRow("Monitors & Accessories", "26 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 11211,
                name = "Asus India",
                subtitle = "ROG Gaming & ZenBook Laptops",
                earnCoinsText = "Upto 16 Coins per ₹100",
                rateDetail = "1.57% Cashback on official store",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/11211/original/New_Project_-_2025-06-10T163442.719.png?1749553509",
                targetUrl = "https://in.store.asus.com/",
                categoryName = "Electronics & Tech Brands",
                cookieDuration = "30 Days",
                payoutRate = "1.575% Per Sale",
                rates = listOf(
                    CampaignRateRow("ROG & TUF Gaming Laptops", "16 Coins per ₹100"),
                    CampaignRateRow("ZenBook & VivoBook", "16 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 5976,
                name = "Cellecor",
                subtitle = "Smart tech, TVs & smartwatches",
                earnCoinsText = "Upto 52 Coins per ₹100",
                rateDetail = "5.25% Cashback on gadgets",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5976/original/New_Project_-_2024-01-17T131304.401.png?1705477531",
                targetUrl = "https://cellecor.com/",
                categoryName = "Electronics & Tech Brands",
                cookieDuration = "30 Days",
                payoutRate = "5.25% Per Sale",
                rates = listOf(
                    CampaignRateRow("Smart TVs & Soundbars", "52 Coins per ₹100"),
                    CampaignRateRow("Wearables & Accessories", "52 Coins per ₹100")
                )
            )
        )
    )

    // 3. Fashion & Beauty
    val fashionCategory = CategoryGroup(
        id = "fashion",
        title = "Fashion & Beauty",
        iconEmoji = "👗",
        description = "Apparel, footwear, beauty & ethnic craftsmanship",
        gradientColors = listOf(Color(0xFFFCE4EC), Color(0xFFFFF3E0)),
        campaigns = listOf(
            CampaignItem(
                id = 9568,
                name = "Ajio",
                subtitle = "Trendy fashion, footwear & accessories",
                earnCoinsText = "Upto 90 Coins per ₹100",
                rateDetail = "9.0% Cashback on fashion orders",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/9568/original/download_%2886%29.jpg?1738763592",
                targetUrl = "https://ajiogram.ajio.com/",
                categoryName = "Fashion & Beauty",
                badge = "HOT TREND",
                cookieDuration = "30 Days",
                payoutRate = "9.0% Per Sale",
                rates = listOf(
                    CampaignRateRow("All Fashion Categories", "90 Coins per ₹100 (9%)"),
                    CampaignRateRow("Footwear & Accessories", "90 Coins per ₹100 (9%)")
                )
            ),
            CampaignItem(
                id = 891,
                name = "Nykaa Beauty",
                subtitle = "100% genuine makeup & skincare",
                earnCoinsText = "Upto 42 Coins per ₹100",
                rateDetail = "4.2% Cashback on beauty",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/891/original/open-uri20260422-2537676-1o7bz54.?1776836644",
                targetUrl = "https://www.nykaa.com/",
                categoryName = "Fashion & Beauty",
                badge = "GENUINE",
                cookieDuration = "30 Days",
                payoutRate = "4.2% Per Sale",
                rates = listOf(
                    CampaignRateRow("Cosmetics & Makeup", "42 Coins per ₹100"),
                    CampaignRateRow("Skincare & Hair Care", "42 Coins per ₹100"),
                    CampaignRateRow("Luxury Fragrances", "42 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 13566,
                name = "AMIRO Beauty",
                subtitle = "Smart light therapy & beauty mirrors",
                earnCoinsText = "Upto 75 Coins per ₹100",
                rateDetail = "7.5% Cashback on tech beauty",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/13566/original/New_Project_-_2026-04-27T130720.447.png?1777275457",
                targetUrl = "https://amirobeauty.com/",
                categoryName = "Fashion & Beauty",
                cookieDuration = "30 Days",
                payoutRate = "7.5% Per Sale",
                rates = listOf(
                    CampaignRateRow("Beauty Devices", "75 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 6566,
                name = "Bacca Bucci",
                subtitle = "Streetwear boots & trendy sneakers",
                earnCoinsText = "Upto 81 Coins per ₹100",
                rateDetail = "8.1% Cashback on footwear",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/6566/original/New_Project_-_2024-05-16T111415.561.png?1715838270",
                targetUrl = "https://baccabucci.com/",
                categoryName = "Fashion & Beauty",
                cookieDuration = "30 Days",
                payoutRate = "8.1% Per Sale",
                rates = listOf(
                    CampaignRateRow("Sneakers & Boots", "81 Coins per ₹100"),
                    CampaignRateRow("Apparel & Accessories", "81 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 3081,
                name = "Jaypore",
                subtitle = "Handcrafted sarees, jewelry & kurtas",
                earnCoinsText = "Upto 90 Coins per ₹100",
                rateDetail = "9.0% Cashback on artisanal fashion",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/3081/original/jaypore-logo.png?1491988365",
                targetUrl = "https://www.jaypore.com/",
                categoryName = "Fashion & Beauty",
                badge = "HANDMADE",
                cookieDuration = "30 Days",
                payoutRate = "9.0% Per Sale",
                rates = listOf(
                    CampaignRateRow("Handloom Sarees & Kurtas", "90 Coins per ₹100"),
                    CampaignRateRow("Silver & Brass Jewelry", "90 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 5184,
                name = "Aldo Shoes",
                subtitle = "Premium footwear & luxury handbags",
                earnCoinsText = "Upto 22 Coins per ₹100",
                rateDetail = "2.25% Cashback on luxury shoes",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5184/original/logo.png?1680695442",
                targetUrl = "https://www.aldoshoes.in/",
                categoryName = "Fashion & Beauty",
                cookieDuration = "30 Days",
                payoutRate = "2.25% Per Sale",
                rates = listOf(
                    CampaignRateRow("Heels, Loafers & Boots", "22 Coins per ₹100"),
                    CampaignRateRow("Handbags & Accessories", "22 Coins per ₹100")
                )
            )
        )
    )

    // 4. Flights & Travel
    val travelCategory = CategoryGroup(
        id = "travel",
        title = "Flights & Travel",
        iconEmoji = "✈️",
        description = "Flight bookings, hotel stays & airline tickets",
        gradientColors = listOf(Color(0xFFE1F5FE), Color(0xFFEDE7F6)),
        campaigns = listOf(
            CampaignItem(
                id = 57507,
                name = "Akasa Air",
                subtitle = "Domestic Flight Tickets across India",
                earnCoinsText = "1,800 Coins",
                rateDetail = "Flat ₹180 on confirmed flight ticket",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/57507/original/Screenshot_2026-05-21_153125.jpg?1779357965",
                targetUrl = "https://www.akasaair.com/",
                categoryName = "Flights & Travel",
                badge = "FLIGHT DEAL",
                cookieDuration = "30 Days",
                payoutRate = "₹180 / Booking",
                rates = listOf(
                    CampaignRateRow("Confirmed Flight Ticket", "1,800 Coins (Flat ₹180)")
                ),
                instructions = listOf(
                    "Book domestic flights on Akasa Air official website.",
                    "Coins tracked within 48 hours of ticket confirmation."
                )
            ),
            CampaignItem(
                id = 5195,
                name = "Air India Express",
                subtitle = "Domestic & Gulf Flight Bookings",
                earnCoinsText = "1,680 Coins",
                rateDetail = "Flat ₹168.75 per ticket booking",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5195/original/logo20250811-2181357-1w4q0tb.jpg?1754903716",
                targetUrl = "https://www.airindiaexpress.com/home",
                categoryName = "Flights & Travel",
                badge = "POPULAR",
                cookieDuration = "30 Days",
                payoutRate = "₹168.75 / Booking",
                rates = listOf(
                    CampaignRateRow("Domestic & International Flights", "1,680 Coins (Flat ₹168.75)")
                )
            ),
            CampaignItem(
                id = 5622,
                name = "Air India",
                subtitle = "National Carrier Domestic & Global",
                earnCoinsText = "1,120 Coins",
                rateDetail = "Flat ₹112.50 per flight booking",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5622/original/New_Project_-_2024-03-20T124625.923.png?1710919013",
                targetUrl = "https://www.airindia.com/en/book-flights/air-india-affiliate-flight-offers",
                categoryName = "Flights & Travel",
                cookieDuration = "30 Days",
                payoutRate = "₹112.50 / Booking",
                rates = listOf(
                    CampaignRateRow("Flight Ticket Booking", "1,120 Coins (Flat ₹112.50)")
                )
            ),
            CampaignItem(
                id = 3801,
                name = "MakeMyTrip",
                subtitle = "Hotel Bookings & Vacation Stays",
                earnCoinsText = "1,120 Coins",
                rateDetail = "Flat ₹112.50 per hotel booking",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/3801/original/logo20250902-1816682-6zb74.jpg?1756809448",
                targetUrl = "https://www.makemytrip.com/hotels",
                categoryName = "Flights & Travel",
                badge = "HOTELS",
                cookieDuration = "30 Days",
                payoutRate = "₹112.50 / Booking",
                rates = listOf(
                    CampaignRateRow("Domestic & International Hotel Stay", "1,120 Coins (Flat ₹112.50)")
                )
            ),
            CampaignItem(
                id = 5281,
                name = "Airpaz",
                subtitle = "Global flights & hotel search engine",
                earnCoinsText = "Upto 30 Coins per ₹100",
                rateDetail = "3.0% Cashback on bookings",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5281/original/New_Project_-_2023-05-22T163516.009.png?1684753539",
                targetUrl = "https://www.airpaz.com/",
                categoryName = "Flights & Travel",
                cookieDuration = "30 Days",
                payoutRate = "3.0% Per Sale",
                rates = listOf(
                    CampaignRateRow("Flight Tickets & Hotels", "30 Coins per ₹100 (3%)")
                )
            )
        )
    )

    // 5. Kids & Home
    val kidsHomeCategory = CategoryGroup(
        id = "kids_home",
        title = "Kids & Home",
        iconEmoji = "👶",
        description = "Baby essentials, furniture, gourmet coffee & home decor",
        gradientColors = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3)),
        campaigns = listOf(
            CampaignItem(
                id = 49,
                name = "Firstcry",
                subtitle = "Baby clothing, diapers, toys & gear",
                earnCoinsText = "225 Coins",
                rateDetail = "Flat ₹22.50 on kids purchases",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/49/original/FC-Logo-big_store_tagline.png?1582718900",
                targetUrl = "https://www.firstcry.com/",
                categoryName = "Kids & Home",
                badge = "#1 BABY STORE",
                cookieDuration = "30 Days",
                payoutRate = "₹22.50 / Sale",
                rates = listOf(
                    CampaignRateRow("Baby Clothes & Toys", "225 Coins (Flat ₹22.50)"),
                    CampaignRateRow("Diapers & Feeding", "225 Coins (Flat ₹22.50)")
                )
            ),
            CampaignItem(
                id = 56288,
                name = "LuvLap",
                subtitle = "Baby strollers, car seats & nursing",
                earnCoinsText = "Upto 67 Coins per ₹100",
                rateDetail = "6.75% Cashback on baby care",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/56288/original/channels4_profile.jpg?1776774659",
                targetUrl = "https://www.luvlap.com/",
                categoryName = "Kids & Home",
                badge = "CERTIFIED",
                cookieDuration = "15 Days",
                payoutRate = "6.75% Per Sale",
                rates = listOf(
                    CampaignRateRow("Strollers & Prams", "67 Coins per ₹100"),
                    CampaignRateRow("Car Seats & High Chairs", "67 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 3705,
                name = "Furlenco",
                subtitle = "Furniture & home appliance rentals",
                earnCoinsText = "2,360 Coins",
                rateDetail = "Flat ₹236.25 per furniture rental",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/3705/original/Furlenco-logo.png?1540902030",
                targetUrl = "https://www.furlenco.com/",
                categoryName = "Kids & Home",
                badge = "RENTAL SAVINGS",
                cookieDuration = "30 Days",
                payoutRate = "₹236.25 / Rental",
                rates = listOf(
                    CampaignRateRow("Living Room & Bedroom Furniture", "2,360 Coins (Flat ₹236.25)")
                )
            ),
            CampaignItem(
                id = 37498,
                name = "Agaro",
                subtitle = "Kitchen OTG, air fryers & personal care",
                earnCoinsText = "Upto 52 Coins per ₹100",
                rateDetail = "5.25% Cashback on appliances",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/37498/original/New_Project_-_2025-12-22T110320.092.png?1766381618",
                targetUrl = "https://agarolifestyle.com/",
                categoryName = "Kids & Home",
                cookieDuration = "30 Days",
                payoutRate = "5.25% Per Sale",
                rates = listOf(
                    CampaignRateRow("Kitchen & Home Appliances", "52 Coins per ₹100"),
                    CampaignRateRow("Massagers & Grooming", "52 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 5555,
                name = "Jaipur Rugs",
                subtitle = "Handcrafted luxury carpets & dhurries",
                earnCoinsText = "Upto 52 Coins per ₹100",
                rateDetail = "5.25% Cashback on designer rugs",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/5555/original/New_Project_-_2023-09-27T182120.046.png?1695819098",
                targetUrl = "https://www.jaipurrugs.com/in/",
                categoryName = "Kids & Home",
                cookieDuration = "30 Days",
                payoutRate = "5.25% Per Sale",
                rates = listOf(
                    CampaignRateRow("Artisanal Hand-Knotted Rugs", "52 Coins per ₹100"),
                    CampaignRateRow("Modern Dhurries & Mats", "52 Coins per ₹100")
                )
            ),
            CampaignItem(
                id = 4670,
                name = "Rage Coffee",
                subtitle = "Vitamins infused instant craft coffee",
                earnCoinsText = "Upto 187 Coins per ₹100",
                rateDetail = "18.75% Cashback on coffee orders",
                logoUrl = "https://cdn0.cuelinks.com/campaigns/4670/original/image_%2832%29.png?1649846164",
                targetUrl = "https://ragecoffee.com/",
                categoryName = "Kids & Home",
                badge = "HIGHEST %",
                cookieDuration = "7 Days",
                payoutRate = "18.75% Per Sale",
                rates = listOf(
                    CampaignRateRow("Flavoured Instant Coffee", "187 Coins per ₹100 (18.75%)"),
                    CampaignRateRow("Cold Brew & Beans", "187 Coins per ₹100 (18.75%)")
                )
            )
        )
    )

    val allCategories: List<CategoryGroup> = listOf(
        financeCategory,
        electronicsCategory,
        fashionCategory,
        travelCategory,
        kidsHomeCategory
    )

    val allCampaigns: List<CampaignItem> = allCategories.flatMap { it.campaigns }

    fun findCampaign(name: String): CampaignItem? {
        val trimmed = name.trim().lowercase()
        return allCampaigns.find { it.name.lowercase() == trimmed }
            ?: allCampaigns.find { it.name.lowercase().contains(trimmed) || trimmed.contains(it.name.lowercase()) }
            ?: allCampaigns.find { it.targetUrl.lowercase().contains(trimmed) }
    }
}
