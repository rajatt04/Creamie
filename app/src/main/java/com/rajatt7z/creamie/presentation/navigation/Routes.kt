package com.rajatt7z.creamie.presentation.navigation

/**
 * All navigation routes for the app.
 */
object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val COLLECTIONS = "collections"
    const val LIBRARY = "library"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"

    const val DETAIL = "detail/{photoId}"
    fun detail(photoId: Int) = "detail/$photoId"

    const val COLLECTION_DETAIL = "collection/{collectionId}"
    fun collectionDetail(collectionId: String) = "collection/$collectionId"

    // Existing widget screens
    const val WIDGETS = "widget"
}
