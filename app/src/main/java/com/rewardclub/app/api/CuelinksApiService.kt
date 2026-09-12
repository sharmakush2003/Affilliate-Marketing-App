// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.api

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
    var CUELINKS_API_KEY: String = com.rewardclub.app.BuildConfig.CUELINKS_API_KEY
    var PUBLISHER_ID: String = com.rewardclub.app.BuildConfig.CUELINKS_CHANNEL_ID
    // ✅ FIX: Aligned with admin dashboard — was "https://api.cuelinks.com/v2" (wrong host)
    const val BASE_URL: String = "https://www.cuelinks.com/api/v2"
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
     * Generate Cuelinks Affiliate Deep Link from a raw Merchant URL with User's Supabase ID attached as subid
     */
    fun createAffiliateLink(
        targetUrl: String,
        userId: String = "",
        channelId: String = CuelinksConfig.PUBLISHER_ID
    ): String {
        val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
        val subParam = if (userId.isNotBlank()) "&subid=${URLEncoder.encode(userId, "UTF-8")}" else ""
        return "https://linksredirect.com/?cid=$channelId&source=api${subParam}&url=$encodedUrl"
    }

    /**
     * 🔥 CRITICAL: Fires a background HTTP ping to Cuelinks tracking server to register
     * the affiliate click SILENTLY. Completely independent of WebView loading.
     */
    suspend fun fireAndForgetClick(
        targetUrl: String,
        userId: String = "",
        channelId: String = CuelinksConfig.PUBLISHER_ID,
        campaignName: String = "Affiliate Store"
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
            val subParam = if (userId.isNotBlank()) "&subid=${URLEncoder.encode(userId, "UTF-8")}" else ""
            val trackingUrl = "https://linksredirect.com/?cid=$channelId&source=api${subParam}&url=$encodedUrl"
            
            // 1. Ping CueLinks tracking server
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

            // 2. Real-time log into Reward Club Backend & Supabase
            try {
                val apiUrl = URL("https://affilliate-marketing-app.vercel.app/api/clicks")
                val apiConn = apiUrl.openConnection() as HttpURLConnection
                apiConn.requestMethod = "POST"
                apiConn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                apiConn.doOutput = true
                apiConn.connectTimeout = 5000
                apiConn.readTimeout = 5000
                val body = org.json.JSONObject().apply {
                    put("campaignName", campaignName)
                    put("destinationUrl", targetUrl)
                    put("subId", userId)
                    put("userId", userId)
                    put("channelId", channelId)
                    put("source", "api")
                    put("platform", "mobile")
                }.toString()
                java.io.OutputStreamWriter(apiConn.outputStream, "UTF-8").use { it.write(body) }
                apiConn.responseCode
                apiConn.disconnect()
            } catch (_: Exception) {}

            if (com.rewardclub.app.BuildConfig.DEBUG) {
                android.util.Log.d("Cuelinks", "Click ping response: $code | SubID: $userId | Campaign: $campaignName")
            }
            code in 200..399
        } catch (e: Exception) {
            if (com.rewardclub.app.BuildConfig.DEBUG) {
                android.util.Log.w("Cuelinks", "Click ping failed: ${e.message}")
            }
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
