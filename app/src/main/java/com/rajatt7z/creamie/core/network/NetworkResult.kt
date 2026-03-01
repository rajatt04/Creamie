package com.rajatt7z.creamie.core.network

/**
 * Sealed class representing the result of a network operation.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
    data object Empty : NetworkResult<Nothing>()
}

/**
 * Extension to map NetworkResult data.
 */
inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): NetworkResult<R> {
    return when (this) {
        is NetworkResult.Success -> NetworkResult.Success(transform(data))
        is NetworkResult.Error -> this
        is NetworkResult.Loading -> this
        is NetworkResult.Empty -> this
    }
}

/**
 * Convert a suspend call to a NetworkResult.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        val result = apiCall()
        NetworkResult.Success(result)
    } catch (e: retrofit2.HttpException) {
        NetworkResult.Error(
            message = e.message() ?: "HTTP error",
            code = e.code()
        )
    } catch (e: java.io.IOException) {
        NetworkResult.Error(message = "Network error: ${e.localizedMessage}")
    } catch (e: Exception) {
        NetworkResult.Error(message = e.localizedMessage ?: "Unknown error")
    }
}
