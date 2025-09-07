package com.rahulyadav.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.rahulyadav.analytics.analytics.AnalyticImp
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AnalyticsInitProviderTest {

    @Test
    fun testContentProviderInitialization() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val provider = AnalyticsInitProvider()
        
        // Test that onCreate returns true (successful initialization)
        val result = provider.onCreate()
        assert(result) { "ContentProvider initialization should succeed" }
        
        // Test that AnalyticImp is initialized
        try {
            val analytics = AnalyticImp.getInstance()
            assert(analytics != null) { "AnalyticImp should be initialized" }
        } catch (e: IllegalStateException) {
            // This is expected if not properly initialized
            assert(false) { "AnalyticImp should be initialized by ContentProvider" }
        }
    }
}
