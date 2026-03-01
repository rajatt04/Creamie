package com.rajatt7z.creamie.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.rajatt7z.creamie.presentation.detail.DetailScreen
import com.rajatt7z.creamie.presentation.home.HomeScreen
import com.rajatt7z.creamie.presentation.library.LibraryScreen
import com.rajatt7z.creamie.presentation.onboarding.OnboardingScreen
import com.rajatt7z.creamie.presentation.search.SearchScreen
import com.rajatt7z.creamie.presentation.settings.SettingsScreen
import com.rajatt7z.creamie.screens.WidgetsScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.SEARCH, "Search", Icons.Filled.Search, Icons.Outlined.Search),
    BottomNavItem(Routes.LIBRARY, "Library", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    BottomNavItem(Routes.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
)

@Composable
fun CreamieNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) }
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onPhotoClick = { photoId -> navController.navigate(Routes.detail(photoId)) },
                    onSearchClick = { navController.navigate(Routes.SEARCH) },
                    onCollectionClick = { id -> navController.navigate(Routes.collectionDetail(id)) }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    onPhotoClick = { photoId -> navController.navigate(Routes.detail(photoId)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.LIBRARY) {
                LibraryScreen(
                    onPhotoClick = { photoId -> navController.navigate(Routes.detail(photoId)) }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen()
            }

            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("photoId") { type = NavType.IntType }),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://wallpaper/{photoId}" })
            ) {
                DetailScreen(
                    onBack = { navController.popBackStack() },
                    onColorSearch = { color ->
                        navController.navigate(Routes.SEARCH)
                    }
                )
            }

            composable(
                route = Routes.COLLECTION_DETAIL,
                arguments = listOf(navArgument("collectionId") { type = NavType.StringType }),
                deepLinks = listOf(navDeepLink { uriPattern = "creamie://collection/{collectionId}" })
            ) {
                // Collection detail screen — reusing search-like grid
                val collectionId = it.arguments?.getString("collectionId") ?: ""
                // TODO: CollectionDetailScreen(collectionId, onBack, onPhotoClick)
            }

            // Keep existing widget screen
            composable(Routes.WIDGETS) {
                WidgetsScreen(navController)
            }

            // Onboarding
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
