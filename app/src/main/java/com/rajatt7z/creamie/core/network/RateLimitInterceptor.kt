package com.rajatt7z.creamie.core.network

import android.util.Log
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import com.rajatt7z.creamie.data.local.datastore.UserPreferencesManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor that tracks Pexels API rate limits.
 * Pexels limits: 200 requests/hour, 20,000 requests/month.
 *
 * Reads X-Ratelimit-Limit and X-Ratelimit-Remaining from response headers
 * and persists usage counts in DataStore.
 */
@Singleton
class RateLimitInterceptor @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : Interceptor {

    companion object {
        private const val TAG = "RateLimitInterceptor"
        private const val HEADER_RATE_LIMIT = "X-Ratelimit-Limit"
        private const val HEADER_RATE_REMAINING = "X-Ratelimit-Remaining"
        private const val HEADER_RATE_RESET = "X-Ratelimit-Reset"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        // Parse rate limit headers from Pexels API
        val rateLimit = response.header(HEADER_RATE_LIMIT)?.toIntOrNull()
        val rateRemaining = response.header(HEADER_RATE_REMAINING)?.toIntOrNull()
        val rateReset = response.header(HEADER_RATE_RESET)?.toLongOrNull()

        if (rateLimit != null && rateRemaining != null) {
            val requestsUsed = rateLimit - rateRemaining
            Log.d(TAG, "API Rate: $rateRemaining/$rateLimit remaining, used=$requestsUsed")

            // Persist to DataStore (non-blocking as best effort)
            runBlocking {
                preferencesManager.updateApiUsage(
                    requestsUsedThisHour = requestsUsed,
                    rateLimitPerHour = rateLimit,
                    resetTimestamp = rateReset ?: 0L
                )
            }
        }

        return response
    }
}
