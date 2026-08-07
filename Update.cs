using System.IO;
using System.Text.RegularExpressions;
using System;

class Program {
    static void Main() {
        string path = @"app\src\main\java\com\rewardclub\app\ui\screens\HomeScreen.kt";
        string content = File.ReadAllText(path);

        // Update data classes
        content = Regex.Replace(content, @"data class BrandEarn\((.*?)\)", "data class BrandEarn($1, val logoUrl: String? = null)", RegexOptions.Singleline);
        content = Regex.Replace(content, @"data class CreditCardMock\((.*?)\)", "data class CreditCardMock($1, val logoUrl: String? = null)", RegexOptions.Singleline);
        content = Regex.Replace(content, @"data class InsuranceMock\((.*?)\)", "data class InsuranceMock($1, val logoUrl: String? = null)", RegexOptions.Singleline);
        content = Regex.Replace(content, @"data class LoanMock\((.*?)\)", "data class LoanMock($1, val logoUrl: String? = null)", RegexOptions.Singleline);

        // Update lists
        content = content.Replace(
            "BrandEarn(\"Amazon\", \"amazon\", \"Upto 12\", \"per ₹100\", Color(0xFFFF9900), R.drawable.amazon_logo)",
            "BrandEarn(\"Amazon\", \"amazon\", \"Upto 12\", \"per ₹100\", Color(0xFFFF9900), R.drawable.amazon_logo, \"https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Amazon_logo.svg/1024px-Amazon_logo.svg.png\")"
        );
        content = content.Replace(
            "BrandEarn(\"Flipkart\", \"Flipkart\", \"Upto 200\", \"per transaction\", Color(0xFF2874F0), R.drawable.flipkart_logo)",
            "BrandEarn(\"Flipkart\", \"Flipkart\", \"Upto 200\", \"per transaction\", Color(0xFF2874F0), R.drawable.flipkart_logo, \"https://upload.wikimedia.org/wikipedia/en/thumb/7/7a/Flipkart_logo.svg/1024px-Flipkart_logo.svg.png\")"
        );
        content = content.Replace(
            "BrandEarn(\"Myntra\", \"Myntra\", \"Upto 10\", \"per ₹100\", Color(0xFFE63956), R.drawable.myntra_logo)",
            "BrandEarn(\"Myntra\", \"Myntra\", \"Upto 10\", \"per ₹100\", Color(0xFFE63956), R.drawable.myntra_logo, \"https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Myntra_logo.png/600px-Myntra_logo.png\")"
        );
        content = content.Replace(
            "BrandEarn(\"HP Pay\", \"HP Pay\", \"Upto 10\", \"per ₹100\", Color(0xFF0033A0), R.drawable.hp_pay_logo)",
            "BrandEarn(\"HP Pay\", \"HP Pay\", \"Upto 10\", \"per ₹100\", Color(0xFF0033A0), R.drawable.hp_pay_logo, \"https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Hewlett-Packard_logo.svg/1024px-Hewlett-Packard_logo.svg.png\")"
        );
        
        // Rewrite BrandCard shape
        content = Regex.Replace(content, @"fun BrandCard\(brand: BrandEarn, onClick: \(\) -> Unit\) \{.*?\}  \}", @"fun BrandCard(brand: BrandEarn, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = White),
        shape = RoundedCornerShape(32.dp, 8.dp, 32.dp, 8.dp),
        border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .width(150.dp)
            .height(200.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.height(60.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (brand.logoUrl != null) {
                    coil.compose.AsyncImage(model = brand.logoUrl, contentDescription = brand.name, modifier = Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
                } else if (brand.logoResId != null) {
                    Image(painter = painterResource(id = brand.logoResId), contentDescription = brand.name, modifier = Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
                } else {
                    Text(text = brand.logoText, color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(brand.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.background(Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = ""🪙"", fontSize = 10.sp)
                    Text(text = ""${brand.earnRate} Coins"", color = DarkGreen, fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = brand.rateDetail, color = TextGray, fontSize = 10.sp, textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}", RegexOptions.Singleline);

        File.WriteAllText(path, content);
    }
}
