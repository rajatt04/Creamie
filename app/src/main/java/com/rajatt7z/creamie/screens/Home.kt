package com.rajatt7z.creamie.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rajatt7z.creamie.ui.theme.CreamieTheme

sealed class Screen(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Dashboard : Screen("dashboard", Icons.Default.Home,"Home")
    object Profile : Screen("profile", Icons.Default.Person,"Profile")
    object Settings : Screen("settings", Icons.Default.Settings,"Settings")
}

@Composable
fun Home() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Remove the windowInsetsPadding for status bar to get normal status bar behavior
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                listOf(
                    Screen.Dashboard,
                    Screen.Profile,
                    Screen.Settings
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
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
        // Let Scaffold handle status bar automatically
        contentWindowInsets = WindowInsets.systemBars
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen(navController) }
            composable(Screen.Profile.route) { ProfileScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen() }
            composable("wallpaper/{imageUrl}") { backStackEntry ->
                val url = backStackEntry.arguments?.getString("imageUrl") ?: ""
                val imageUrl = Uri.decode(url)
                WallpaperScreen(imageUrl)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    CreamieTheme {
        Home()
    }
}