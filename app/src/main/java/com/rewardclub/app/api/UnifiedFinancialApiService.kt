// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
package com.rewardclub.app.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Unified Financial Products API Service
 * Replaces legacy affiliate networks (Cuelinks) with direct Server-to-Server
 * embedded banking, personal loans, and credit lines architecture.
 */
object UnifiedFinancialConfig {
    var BASE_URL: String = "https://affilliate-marketing-app.vercel.app/api/financial"
    var API_KEY: String = "rc_live_unified_fin_2026"
}

data class FinancialProduct(
    val id: String,
    val name: String,
    val category: String, // "PERSONAL_LOAN", "CREDIT_CARD", "INSURANCE"
    val provider: String,
    val minAmount: Long = 5000L,
    val maxAmount: Long = 1000000L,
    val interestRatePerAnnum: Double = 10.5,
    val commissionPercent: Double = 3.0,
    val coinRewardRate: String = "Upto 3% Coins",
    val applyUrl: String
)

data class LeadSubmissionResult(
    val referenceId: String,
    val status: String, // "SUBMITTED", "APPROVED", "KYC_PENDING", "DISBURSED"
    val redirectUrl: String? = null,
    val message: String
)

class UnifiedFinancialApiService {

    /**
     * Records a click and initiates a tracking session directly with Reward Club's backend & Unified API.
     * ZERO external Cuelinks hops or cookie-blocking redirects.
     */
    suspend fun trackClick(
        targetUrl: String,
        userId: String = "",
        campaignName: String = "Direct Partner"
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val apiUrl = URL("https://affilliate-marketing-app.vercel.app/api/clicks")
            val apiConn = apiUrl.openConnection() as HttpURLConnection
            apiConn.requestMethod = "POST"
            apiConn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            apiConn.doOutput = true
            apiConn.connectTimeout = 5000
            apiConn.readTimeout = 5000
            val body = JSONObject().apply {
                put("campaignName", campaignName)
                put("destinationUrl", targetUrl)
                put("userId", userId)
                put("subId", userId)
                put("provider", "UNIFIED_FINANCIAL_API")
                put("timestamp", System.currentTimeMillis())
            }
            apiConn.outputStream.use { os ->
                os.write(body.toString().toByteArray(Charsets.UTF_8))
            }
            apiConn.responseCode in 200..299
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Submit lead directly to Unified Financial API (/v1/leads)
     */
    suspend fun createLead(
        productId: String,
        userId: String,
        mobileNumber: String = "",
        panNumber: String = ""
    ): LeadSubmissionResult = withContext(Dispatchers.IO) {
        val refId = "RC-LEAD-${System.currentTimeMillis()}"
        try {
            // Background lead ingestion to server
            val url = URL("${UnifiedFinancialConfig.BASE_URL}/v1/leads")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer ${UnifiedFinancialConfig.API_KEY}")
            conn.doOutput = true
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val body = JSONObject().apply {
                put("productId", productId)
                put("userId", userId)
                put("referenceId", refId)
                if (mobileNumber.isNotBlank()) put("mobile", mobileNumber)
                if (panNumber.isNotBlank()) put("pan", panNumber)
            }
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            val code = conn.responseCode
            conn.disconnect()
            LeadSubmissionResult(
                referenceId = refId,
                status = if (code in 200..299) "SUBMITTED" else "PENDING",
                message = "Application tracked securely"
            )
        } catch (e: Exception) {
            LeadSubmissionResult(
                referenceId = refId,
                status = "PENDING",
                message = "Lead logged offline/pending sync"
            )
        }
    }
}
