package com.cuelinks.sdk

import android.content.Context
import android.content.pm.PackageManager
import java.net.URLEncoder

/**
 * Native Cuelinks SDK Engine Implementation
 * Fulfills Cuelinks Android SDK 1.0.3 API Contract
 */
object Cuelinks {

    @JvmStatic
    var channelId: String = "301603"
        private set

    @JvmStatic
    fun initialize(context: Context) {
        try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            val bundle = appInfo.metaData
            if (bundle != null) {
                val configuredId = bundle.getString("com.cuelinks.channelId")
                    ?: bundle.getInt("com.cuelinks.channelId").toString()
                if (configuredId.isNotEmpty() && configuredId != "0") {
                    channelId = configuredId
                }
            }
        } catch (e: Exception) {
            if (com.rewardclub.app.BuildConfig.DEBUG) {
                android.util.Log.e("CuelinksSDK", "Failed to initialize from manifest meta-data", e)
            }
        }
    }

    @JvmStatic
    fun getAffiliateUrl(targetUrl: String, subId: String = ""): String {
        val encodedUrl = URLEncoder.encode(targetUrl, "UTF-8")
        val subIdParam = if (subId.isNotEmpty()) "&subid=$subId" else ""
        return "https://linksredirect.com/?cid=$channelId&source=api$subIdParam&url=$encodedUrl"
    }
}
