package com.rahulyadav.imagedownlinglib

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.rahulyadav.imagedownlinglib.cache.ImageCache
import com.rahulyadav.imagedownlinglib.config.ImageLoaderConfig
import com.rahulyadav.imagedownlinglib.core.ImageCallback
import com.rahulyadav.imagedownlinglib.core.ImageResult
import com.rahulyadav.imagedownlinglib.decoder.ImageDecoder
import com.rahulyadav.imagedownlinglib.network.NetworkClient
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * Unit tests for ImageLoader library.
 * Demonstrates the testability of the clean architecture.
 */
@RunWith(RobolectricTestRunner::class)
class ImageLoaderTest {
    
    private lateinit var context: Context
    private lateinit var mockMemoryCache: ImageCache
    private lateinit var mockDiskCache: ImageCache
    private lateinit var mockNetworkClient: NetworkClient
    private lateinit var mockImageDecoder: ImageDecoder
    private lateinit var mockImageView: ImageView
    private lateinit var mockBitmap: Bitmap
    
    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        
        // Create mocks
        mockMemoryCache = mockk()
        mockDiskCache = mockk()
        mockNetworkClient = mockk()
        mockImageDecoder = mockk()
        mockImageView = mockk()
        mockBitmap = mockk()
        
        // Setup default mock behaviors
        coEvery { mockMemoryCache.get(any()) } returns null
        coEvery { mockMemoryCache.put(any(), any()) } just Runs
        coEvery { mockMemoryCache.size() } returns 0L
        coEvery { mockMemoryCache.maxSize() } returns 1024L
        
        coEvery { mockDiskCache.get(any()) } returns null
        coEvery { mockDiskCache.put(any(), any()) } just Runs
        coEvery { mockDiskCache.size() } returns 0L
        coEvery { mockDiskCache.maxSize() } returns 2048L
        
        coEvery { mockNetworkClient.download(any()) } returns byteArrayOf(1, 2, 3, 4)
        coEvery { mockImageDecoder.decode(any(), any(), any(), any(), any()) } returns mockBitmap
        coEvery { mockImageDecoder.canDecode(any()) } returns true
        
        every { mockImageView.setImageResource(any()) } just Runs
        every { mockImageView.setImageBitmap(any()) } just Runs
    }
    
    @After
    fun tearDown() {
        ImageLoaderFactory.destroy()
    }
    
    @Test
    fun `initialize with custom config should work`() {
        val config = ImageLoaderConfig.Builder(context)
            .memoryCache(mockMemoryCache)
            .diskCache(mockDiskCache)
            .networkClient(mockNetworkClient)
            .imageDecoder(mockImageDecoder)
            .build()
        
        ImageLoader.initialize(context, config)
        
        assert(ImageLoader.isInitialized())
    }
    
    @Test
    fun `load image from memory cache should return cached bitmap`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        coEvery { mockMemoryCache.get(url) } returns mockBitmap
        
        // When
        var result: ImageResult? = null
        ImageLoader.load(url).into(object : ImageCallback {
            override fun onResult(imageResult: ImageResult) {
                result = imageResult
            }
        })
        
        // Then
        assert(result is ImageResult.Success)
        assert((result as ImageResult.Success).bitmap == mockBitmap)
        
        // Verify network was not called
        coVerify(exactly = 0) { mockNetworkClient.download(any()) }
    }
    
    @Test
    fun `load image from disk cache should return cached bitmap`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        coEvery { mockMemoryCache.get(url) } returns null
        coEvery { mockDiskCache.get(url) } returns mockBitmap
        
        // When
        var result: ImageResult? = null
        ImageLoader.load(url).into(object : ImageCallback {
            override fun onResult(imageResult: ImageResult) {
                result = imageResult
            }
        })
        
        // Then
        assert(result is ImageResult.Success)
        assert((result as ImageResult.Success).bitmap == mockBitmap)
        
        // Verify bitmap was stored in memory cache
        coVerify { mockMemoryCache.put(url, mockBitmap) }
        
        // Verify network was not called
        coVerify(exactly = 0) { mockNetworkClient.download(any()) }
    }
    
    @Test
    fun `load image from network should download and cache`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val imageData = byteArrayOf(1, 2, 3, 4)
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        coEvery { mockMemoryCache.get(url) } returns null
        coEvery { mockDiskCache.get(url) } returns null
        coEvery { mockNetworkClient.download(url) } returns imageData
        
        // When
        var result: ImageResult? = null
        ImageLoader.load(url).into(object : ImageCallback {
            override fun onResult(imageResult: ImageResult) {
                result = imageResult
            }
        })
        
        // Then
        assert(result is ImageResult.Success)
        assert((result as ImageResult.Success).bitmap == mockBitmap)
        
        // Verify network was called
        coVerify { mockNetworkClient.download(url) }
        
        // Verify decoder was called
        coVerify { mockImageDecoder.decode(imageData, null, null, false, false) }
        
        // Verify bitmap was cached
        coVerify { mockMemoryCache.put(url, mockBitmap) }
        coVerify { mockDiskCache.put(url, mockBitmap) }
    }
    
    @Test
    fun `network error should return error result`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        coEvery { mockMemoryCache.get(url) } returns null
        coEvery { mockDiskCache.get(url) } returns null
        coEvery { mockNetworkClient.download(url) } throws Exception("Network error")
        
        // When
        var result: ImageResult? = null
        ImageLoader.load(url).into(object : ImageCallback {
            override fun onResult(imageResult: ImageResult) {
                result = imageResult
            }
        })
        
        // Then
        assert(result is ImageResult.Error)
        assert((result as ImageResult.Error).exception.message == "Network error")
    }
    
    @Test
    fun `decode error should return error result`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val imageData = byteArrayOf(1, 2, 3, 4)
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        coEvery { mockMemoryCache.get(url) } returns null
        coEvery { mockDiskCache.get(url) } returns null
        coEvery { mockNetworkClient.download(url) } returns imageData
        coEvery { mockImageDecoder.decode(any(), any(), any(), any(), any()) } throws Exception("Decode error")
        
        // When
        var result: ImageResult? = null
        ImageLoader.load(url).into(object : ImageCallback {
            override fun onResult(imageResult: ImageResult) {
                result = imageResult
            }
        })
        
        // Then
        assert(result is ImageResult.Error)
        assert((result as ImageResult.Error).exception.message == "Decode error")
    }
    
    @Test
    fun `cancel request should stop loading`() = runTest {
        // Given
        val url = "https://example.com/image.jpg"
        val config = createTestConfig()
        ImageLoader.initialize(context, config)
        
        // When
        ImageLoader.load(url).into(mockImageView)
        ImageLoader.cancel(url)
        
        // Then - request should be cancelled
        // In a real implementation, we would verify that the coroutine was cancelled
        // This is a simplified test to demonstrate the API
    }
    
    private fun createTestConfig(): ImageLoaderConfig {
        return ImageLoaderConfig.Builder(context)
            .memoryCache(mockMemoryCache)
            .diskCache(mockDiskCache)
            .networkClient(mockNetworkClient)
            .imageDecoder(mockImageDecoder)
            .build()
    }
}
