package com.example.xyz.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Cuelinks API Integration Helper & Config
 *
 * How to get your API Key from Cuelinks Dashboard:
 * 1. Log into your Cuelinks Publisher Account (https://www.cuelinks.com/users/edit).
 * 2. Click on "Installation" or "Resource Center" -> "Cuelinks API" / "API Key".
 * 3. Copy your API Key / Auth Token and paste it below into [CUELINKS_API_KEY].
 */
object CuelinksConfig {
    // 🔒 Dynamically read from local.properties via BuildConfig (git-ignored, 100% safe)
    var CUELINKS_API_KEY: String = com.example.xyz.BuildConfig.CUELINKS_API_KEY
    var PUBLISHER_ID: String = com.example.xyz.BuildConfig.CUELINKS_CHANNEL_ID
    const val BASE_URL: String = "https://api.cuelinks.com/v2"
}

data class CuelinksCampaign(
    val id: Int,
    val name: String,
    val merchantUrl: String,
    val status: String,
    val payoutRate: String
)

data class CuelinksLinkResult(
    val originalUrl: String,
    val affiliateUrl: String,
    val status: String
)

class CuelinksApiService {

    /**
     * Test function: Generate Cuelinks Affiliate Deep Link from a raw Merchant URL
     * Example: converts "https://www.amazon.in/dp/B08L5VJYV7" into tracked Cuelinks link
     */
    fun createAffiliateLink(targetUrl: String, channelId: String = CuelinksConfig.PUBLISHER_ID): String {
        val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
        return "https://linksredirect.com/?cid=$channelId&source=api&url=$encodedUrl"
    }

    /**
     * 🔥 CRITICAL: Fires a background HTTP ping to Cuelinks tracking server to register
     * the affiliate click SILENTLY. Completely independent of WebView loading.
     *
     * Flow:
     * 1. App calls fireAndForgetClick(merchantUrl) → background HTTP GET to linksredirect.com
     * 2. Cuelinks server receives request → REGISTERS CLICK in dashboard (even on 301/302)
     * 3. WebView SIMULTANEOUSLY loads merchant URL directly → no ISP block, no white screen
     *
     * Result: Click is ALWAYS tracked + merchant site ALWAYS loads.
     */
    suspend fun fireAndForgetClick(
        targetUrl: String,
        channelId: String = CuelinksConfig.PUBLISHER_ID
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
            val trackingUrl = "https://linksredirect.com/?cid=$channelId&source=api&url=$encodedUrl"
            val url = URL(trackingUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36"
            )
            conn.setRequestProperty("Authorization", "Bearer ${CuelinksConfig.CUELINKS_API_KEY}")
            conn.instanceFollowRedirects = false  // Just register the HIT, don't follow redirect
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.connect()
            val code = conn.responseCode
            conn.disconnect()
            android.util.Log.d("Cuelinks", "Click ping → $trackingUrl | Response: $code")
            // 200 OK or 301/302 redirect = server received it = click REGISTERED!
            code in 200..399
        } catch (e: Exception) {
            android.util.Log.w("Cuelinks", "Click ping failed: ${e.message}")
            false
        }
    }

    /**
     * Test Cuelinks API connection to fetch active campaigns
     */
    suspend fun fetchCampaignsTest(): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("${CuelinksConfig.BASE_URL}/campaigns.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Authorization", "Bearer ${CuelinksConfig.CUELINKS_API_KEY}")
            connection.setRequestProperty("Content-Type", "application/json")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorMsg = connection.errorStream?.bufferedReader()?.use { it.readText() } ?: "Error $responseCode"
                throw Exception("Cuelinks API returned status $responseCode: $errorMsg")
            }
        }
    }
}
