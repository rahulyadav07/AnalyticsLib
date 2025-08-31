package com.rahulyadav.analytics

import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

class ExponentialBackoffRetry(private val maxAttempts: Int = 3,
                              private val baseDelayMs: Long = 1000L,
                              private val maxDelayMs: Long = 30000L
) {

    suspend fun <T> execute(operation: suspend () -> T): T {
        var lastException: Exception? = null

        repeat(maxAttempts) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                lastException = e

                if (attempt == maxAttempts - 1) {
                    throw e
                }

                // Calculate delay with exponential backoff and jitter
                val exponentialDelay = baseDelayMs * (2.0.pow(attempt)).toLong()
                val jitter = Random.nextLong(0, exponentialDelay / 4)
                val totalDelay = min(exponentialDelay + jitter, maxDelayMs)

                delay(totalDelay)
            }
        }

        throw lastException ?: Exception("Unknown error in retry mechanism")
    }
}