package com.rajatt7z.creamie.core.common

object Constants {
    const val PEXELS_BASE_URL = "https://api.pexels.com/"

    // Cache
    const val HTTP_CACHE_SIZE = 50L * 1024 * 1024 // 50MB
    const val IMAGE_DISK_CACHE_SIZE = 500L * 1024 * 1024 // 500MB

    // Paging
    const val DEFAULT_PAGE_SIZE = 20
    const val INITIAL_LOAD_SIZE = 40

    // Animations
    const val CROSSFADE_DURATION = 400
    const val SEARCH_DEBOUNCE_MS = 300L
    const val SHIMMER_DURATION_MS = 1200

    // Downloads
    const val DOWNLOAD_FOLDER = "Creamie"

    // Pexels rate limits
    const val RATE_LIMIT_PER_HOUR = 200
    const val RATE_LIMIT_PER_MONTH = 20_000

    // Pexels supported colors for search filter
    val PEXELS_COLORS = listOf(
        "red", "orange", "yellow", "green", "turquoise", "blue",
        "violet", "pink", "brown", "black", "gray", "white"
    )

    // Pexels supported orientations
    val PEXELS_ORIENTATIONS = listOf("landscape", "portrait", "square")

    // Pexels supported sizes
    val PEXELS_SIZES = listOf("large", "medium", "small")

    // Quality options
    val QUALITY_OPTIONS = listOf("original", "large2x", "large", "medium", "small", "tiny")
}
