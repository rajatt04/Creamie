package com.rajatt7z.creamie.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rajatt7z.creamie.presentation.collections.CollectionDetailsScreen
import com.rajatt7z.creamie.presentation.collections.CollectionsScreen
import com.rajatt7z.creamie.presentation.detail.DetailScreen
import com.rajatt7z.creamie.presentation.detail.VideoPlayerScreen
import com.rajatt7z.creamie.presentation.home.HomeScreen
import com.rajatt7z.creamie.presentation.library.LibraryScreen
import com.rajatt7z.creamie.presentation.onboarding.OnboardingScreen
import com.rajatt7z.creamie.presentation.profile.PhotographerProfileScreen
import com.rajatt7z.creamie.presentation.search.PhotoSearchScreen
import com.rajatt7z.creamie.presentation.search.SearchScreen
import com.rajatt7z.creamie.presentation.settings.SettingsScreen
import com.rajatt7z.creamie.presentation.shorts.ShortsFeedScreen
import com.rajatt7z.creamie.screens.WidgetsScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DISCOVER, "Discover", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.SEARCH, "Search", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Routes.SHORTS, "Shorts", Icons.Filled.PlayArrow, Icons.Outlined.PlayArrow),
    BottomNavItem(Routes.COLLECTIONS, "Collections", Icons.Filled.VideoLibrary, Icons.Outlined.VideoLibrary),
    BottomNavItem(Routes.LIBRARY, "Library", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
)

@Composable
fun CreamieNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.DISCOVER,
    onOnboardingComplete: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    com.rajatt7z.creamie.presentation.components.AnimatedBottomNavigationBar(
                        items = bottomNavItems,
                        currentRoute = currentRoute,
                        onItemClick = { item ->
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize(),
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            composable(Routes.DISCOVER) {
                HomeScreen(
                    onPhotoClick = { photoId -> navController.navigate(Routes.photoDetail(photoId)) },
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                    onCollectionClick = { id, title -> navController.navigate(Routes.collectionDetail(id, title)) }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    onSearchPhotos = { query -> navController.navigate(Routes.photoSearch(query)) }
                )
            }
            
            composable(Routes.SHORTS) {
                ShortsFeedScreen()
            }
            
            composable(Routes.COLLECTIONS) {
                CollectionsScreen(
                    onCollectionClick = { id, title -> navController.navigate(Routes.collectionDetail(id, title)) }
                )
            }

            composable(Routes.LIBRARY) {
                LibraryScreen(
                    onPhotoClick = { photoId -> navController.navigate(Routes.photoDetail(photoId)) }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
            
            composable(Routes.CURATED_PHOTOS) {
                // Placeholder for Curated Photos grid
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Curated Photos (Placeholder)") }
            }
            
            composable(
                route = Routes.PHOTO_SEARCH,
                arguments = listOf(navArgument("query") { type = NavType.StringType })
            ) {
                PhotoSearchScreen(
                    onBack = { navController.popBackStack() },
                    onPhotoClick = { photoId -> navController.navigate(Routes.photoDetail(photoId)) }
                )
            }



            composable(
                route = Routes.PHOTO_DETAIL,
                arguments = listOf(navArgument("photoId") { type = NavType.IntType }),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://wallpaper/{photoId}" })
            ) { _ ->
                DetailScreen(
                    onBack = { navController.popBackStack() },
                    onColorSearch = { color ->
                        navController.navigate(Routes.photoSearch(android.net.Uri.encode(color)))
                    },
                    onPhotographerClick = { name ->
                        navController.navigate(Routes.photographerProfile(name))
                    }
                )
            }

            composable(
                route = Routes.VIDEO_PLAYER,
                arguments = listOf(navArgument("videoId") { type = NavType.IntType }),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://video/{videoId}" })
            ) { _ ->
                VideoPlayerScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.COLLECTION_DETAIL,
                arguments = listOf(
                    navArgument("collectionId") { type = NavType.StringType },
                    navArgument("collectionTitle") { type = NavType.StringType }
                ),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://collection/{collectionId}/{collectionTitle}" })
            ) { _ ->
                CollectionDetailsScreen(
                    onBack = { navController.popBackStack() },
                    onPhotoClick = { photoId -> navController.navigate(Routes.photoDetail(photoId)) }
                )
            }
            
            composable(
                route = Routes.PHOTOGRAPHER_PROFILE,
                arguments = listOf(navArgument("photographerName") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://profile/{photographerName}" })
            ) { _ ->
                PhotographerProfileScreen(
                    onBack = { navController.popBackStack() },
                    onPhotoClick = { photoId -> navController.navigate(Routes.photoDetail(photoId)) }
                )
            }

            // Existing widget screen
            composable(Routes.WIDGETS) {
                WidgetsScreen(navController)
            }

            // Onboarding
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onComplete = {
                        onOnboardingComplete()
                        navController.navigate(Routes.DISCOVER) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
