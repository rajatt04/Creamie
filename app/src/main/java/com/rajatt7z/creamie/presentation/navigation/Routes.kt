package com.rajatt7z.creamie.presentation.navigation

/**
 * All navigation routes for the app.
 */
object Routes {
    // Bottom Navigation Destinations
    const val DISCOVER = "discover" // Replaces old "home" conceptually, but we can keep the route name "discover"
    const val SEARCH = "search"
    const val SHORTS = "shorts"
    const val COLLECTIONS = "collections"
    const val LIBRARY = "library"

    // Other Top-Level
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"

    // Media Browsing Grids
    const val CURATED_PHOTOS = "curated_photos"
    const val PHOTO_SEARCH = "photo_search/{query}" // Deep search for photos
    fun photoSearch(query: String) = "photo_search/$query"


    // Details Path
    const val PHOTO_DETAIL = "detail/photo/{photoId}"
    fun photoDetail(photoId: Int) = "detail/photo/$photoId"

    const val VIDEO_PLAYER = "detail/video/{videoId}"
    fun videoPlayer(videoId: Int) = "detail/video/$videoId"

    const val COLLECTION_DETAIL = "collection/{collectionId}/{collectionTitle}"
    fun collectionDetail(collectionId: String, title: String) = "collection/$collectionId/$title"

    const val PHOTOGRAPHER_PROFILE = "profile/{photographerName}"
    fun photographerProfile(name: String) = "profile/$name"

    // Existing widget screens
    const val WIDGETS = "widget"
}
