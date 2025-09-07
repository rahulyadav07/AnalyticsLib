package com.rahulyadav.analytics

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.rahulyadav.analytics.analytics.AnalyticImp
import com.rahulyadav.analytics.analytics.AnalyticsConfig
import com.rahulyadav.analytics.network.RetrofitAnalyticProvider

class AnalyticsInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        // Initialize analytics when ContentProvider is created
        initializeAnalytics()
        return true
    }

    private fun initializeAnalytics() {
        val config = AnalyticsConfig(
            batchSize = 10,
            batchTimeInterval = 5 * 60 * 1000, // 5 minutes
            maxQueueSize = 1000,
            maxRetryAttempts = 3,
            providers = listOf(
                RetrofitAnalyticProvider(
                    endpoint = "https://api.example.com",
                    apiKey = null // Will be set later via init()
                )
            )
        )
        
        AnalyticImp.init(context!!, config)
    }

    // ContentProvider required methods (not used for analytics)
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0
}
