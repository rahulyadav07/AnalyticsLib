package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.analytics.AnalyticsEvent
import com.rahulyadav.analytics.analytics.AnalyticsProvider
import com.rahulyadav.analytics.analytics.DeviceInfo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAnalyticProvider : AnalyticsProvider {

    // Hardcoded configuration - no need to pass from outside
    private val endpoint = "https://api.analytics.rahulyadav.com/"
    private val apiKey = "rahul_analytics_key_2024_secure_token"
    
    private val apiService: AnalyticsApiService by lazy { createApiService() }

    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return try {
            val authHeader = "Bearer $apiKey"
            val request = EventsBatchRequest(events = events)

            val response = apiService.sendEvents(
                authorization = authHeader,
                request = request
            )

            response.isSuccessful
        } catch (e: Exception) {
            println("Retrofit Analytics Error: ${e.message}")
            false
        }
    }

    override fun getName(): String = "Retrofit Analytics Provider"

    private fun createApiService(): AnalyticsApiService {
        val deviceInfo = getDeviceInfo()
        
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .apply {
                // Add custom headers interceptor
                addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val newRequest = originalRequest.newBuilder()
                        .header("X-Analytics-SDK", "RahulAnalytics")
                        .header("X-SDK-Version", "1.0.0")
                        .header("X-Device-Version", deviceInfo.version ?: "Unknown")
                        .header("X-Device-Model", deviceInfo.model ?: "Unknown")
                        .header("X-Device-Manufacturer", deviceInfo.manufacturer ?: "Unknown")
                        .header("X-Device-OS-Version", deviceInfo.osVersion ?: "Unknown")
                        .build()
                    chain.proceed(newRequest)
                }
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(endpoint)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnalyticsApiService::class.java)
    }
    
    private fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            version = android.os.Build.VERSION.RELEASE,
            model = android.os.Build.MODEL,
            manufacturer = android.os.Build.MANUFACTURER,
            osVersion = android.os.Build.VERSION.SDK_INT.toString()
        )
    }
}