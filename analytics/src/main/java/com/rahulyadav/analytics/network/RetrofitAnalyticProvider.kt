package com.rahulyadav.analytics.network

import com.rahulyadav.analytics.analytics.AnalyticsEvent
import com.rahulyadav.analytics.analytics.AnalyticsProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitAnalyticProvider(
    private val endpoint: String,
    private val apiKey: String? = null
) : AnalyticsProvider {

    private val apiService: AnalyticsApiService by lazy { createApiService() }

    override suspend fun sendEvents(events: List<AnalyticsEvent>): Boolean {
        return try {
            val authHeader = apiKey?.let { "Bearer $it" }
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
}