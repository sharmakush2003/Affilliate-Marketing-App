// © 2026 Reward Club. Owner: Puran Dhakad. All rights reserved.
// ✅ F-01 + F-02 SECURITY FIX:
//    - SMTP credentials REMOVED from Android app entirely.
//    - OTP is now generated & verified SERVER-SIDE via the Reward Club OTP Backend.
//    - This file replaces EmailSender.kt (which has been deleted).
package com.rewardclub.app.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Calls the Reward Club OTP backend server (deployed on Render.com).
 * ✅ No SMTP credentials in this file — they live only in the server's .env
 * ✅ OTP is generated and verified SERVER-SIDE — not in the Android process
 */
object OtpApiClient {

    private val SERVER_URL: String get() = com.rewardclub.app.BuildConfig.OTP_SERVER_URL
    private val API_SECRET: String get() = com.rewardclub.app.BuildConfig.OTP_API_SECRET

    data class OtpResult(val success: Boolean, val message: String)
    data class VerifyResult(val success: Boolean, val verified: Boolean, val message: String, val remaining: Int = 0)

    /**
     * Sends a POST to /api/send-otp on the backend.
     * Server generates the OTP, stores it, and sends the email.
     * The OTP value NEVER reaches the Android client.
     */
    suspend fun sendOtp(email: String): OtpResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("$SERVER_URL/api/send-otp")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.setRequestProperty("X-Api-Secret", API_SECRET)
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            val body = JSONObject().apply { put("email", email) }.toString()
            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body) }

            val code = conn.responseCode
            val responseBody = if (code in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                conn.errorStream?.bufferedReader()?.readText() ?: ""
            }
            conn.disconnect()

            if (code == 200) {
                OtpResult(success = true, message = "OTP sent to your email.")
            } else {
                val msg = runCatching { JSONObject(responseBody).getString("error") }
                    .getOrDefault("Failed to send OTP. Please try again.")
                OtpResult(success = false, message = msg)
            }
        } catch (e: Exception) {
            if (com.rewardclub.app.BuildConfig.DEBUG) {
                Log.e("OtpApiClient", "sendOtp failed: ${e.message}", e)
            }
            OtpResult(success = false, message = "Network error. Please check your connection.")
        }
    }

    /**
     * Sends a POST to /api/verify-otp on the backend.
     * Server compares the submitted OTP against the stored value.
     * Returns verified=true only if correct and not expired.
     */
    suspend fun verifyOtp(email: String, otp: String): VerifyResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("$SERVER_URL/api/verify-otp")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.setRequestProperty("X-Api-Secret", API_SECRET)
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            val body = JSONObject().apply {
                put("email", email)
                put("otp", otp)
            }.toString()
            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body) }

            val code = conn.responseCode
            val responseBody = if (code in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                conn.errorStream?.bufferedReader()?.readText() ?: ""
            }
            conn.disconnect()

            val json = runCatching { JSONObject(responseBody) }.getOrNull()

            when (code) {
                200 -> VerifyResult(success = true, verified = true, message = "Sign In Successful!")
                401 -> {
                    val msg = json?.optString("error") ?: "Incorrect OTP."
                    val remaining = json?.optInt("remaining", 0) ?: 0
                    VerifyResult(success = false, verified = false, message = msg, remaining = remaining)
                }
                410 -> VerifyResult(success = false, verified = false, message = "OTP has expired. Please request a new one.")
                429 -> VerifyResult(success = false, verified = false, message = "Too many wrong attempts. Please request a new OTP.")
                404 -> VerifyResult(success = false, verified = false, message = "OTP not found. Please request a new OTP.")
                else -> {
                    val msg = json?.optString("error") ?: "Verification failed. Please try again."
            VerifyResult(success = false, verified = false, message = msg)
                }
            }
        } catch (e: Exception) {
            if (com.rewardclub.app.BuildConfig.DEBUG) {
                Log.e("OtpApiClient", "verifyOtp failed: ${e.message}", e)
            }
            VerifyResult(success = false, verified = false, message = "Network error. Please check your connection.")
        }
    }

    /**
     * Sends a POST to /api/send-support-email on the backend.
     * Tells the server to send a support request email in the background.
     */
    suspend fun sendSupportEmail(
        name: String,
        email: String,
        mobile: String,
        userId: String,
        feedbackType: String,
        rating: Int,
        message: String
    ): OtpResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val url = URL("$SERVER_URL/api/send-support-email")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            conn.setRequestProperty("X-Api-Secret", API_SECRET)
            conn.doOutput = true
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000

            val body = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("mobile", mobile)
                put("userId", userId)
                put("feedbackType", feedbackType)
                put("rating", rating)
                put("message", message)
            }.toString()
            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body) }

            val code = conn.responseCode
            val responseBody = if (code in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                conn.errorStream?.bufferedReader()?.readText() ?: ""
            }
            conn.disconnect()

            if (code == 200) {
                OtpResult(success = true, message = "Support request submitted successfully.")
            } else {
                val msg = runCatching { JSONObject(responseBody).getString("error") }
                    .getOrDefault("Failed to submit support request. Please try again.")
                OtpResult(success = false, message = msg)
            }
        } catch (e: Exception) {
            if (com.rewardclub.app.BuildConfig.DEBUG) {
                Log.e("OtpApiClient", "sendSupportEmail failed: ${e.message}", e)
            }
            OtpResult(success = false, message = "Network error. Please check your connection.")
        }
    }
}

