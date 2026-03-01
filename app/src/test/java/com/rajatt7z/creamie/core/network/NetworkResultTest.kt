package com.rajatt7z.creamie.core.network

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkResultTest {

    @Test
    fun `Success contains data`() {
        val result = NetworkResult.Success("hello")
        assertEquals("hello", result.data)
    }

    @Test
    fun `Error contains message and optional code`() {
        val result = NetworkResult.Error("Not found", 404)
        assertEquals("Not found", result.message)
        assertEquals(404, result.code)
    }

    @Test
    fun `Error without code defaults to null`() {
        val result = NetworkResult.Error("Failure")
        assertEquals("Failure", result.message)
        assertNull(result.code)
    }

    @Test
    fun `safeApiCall returns Success on successful response`() = runTest {
        val result = safeApiCall {
            "data"
        }
        assertTrue(result is NetworkResult.Success)
        assertEquals("data", (result as NetworkResult.Success).data)
    }

    @Test
    fun `safeApiCall returns Error on unsuccessful response`() = runTest {
        val result = safeApiCall<String> {
            throw retrofit2.HttpException(Response.error<String>(404, "Not Found".toResponseBody()))
        }
        assertTrue(result is NetworkResult.Error)
        assertEquals(404, (result as NetworkResult.Error).code)
    }

    @Test
    fun `safeApiCall returns Error on exception`() = runTest {
        val result = safeApiCall<String> {
            throw java.io.IOException("Network failure")
        }
        assertTrue(result is NetworkResult.Error)
        assertTrue((result as NetworkResult.Error).message.contains("Network failure"))
    }

    @Test
    fun `safeApiCall returns Success for null body`() = runTest {
        val result = safeApiCall<String?> {
            null
        }
        // Null body on success should still be Success with null data
        assertTrue(result is NetworkResult.Success)
        assertNull((result as NetworkResult.Success).data)
    }

    @Test
    fun `Loading is a valid state`() {
        val result: NetworkResult<String> = NetworkResult.Loading
        assertTrue(result is NetworkResult.Loading)
    }

    @Test
    fun `Empty is a valid state`() {
        val result: NetworkResult<String> = NetworkResult.Empty
        assertTrue(result is NetworkResult.Empty)
    }
}
