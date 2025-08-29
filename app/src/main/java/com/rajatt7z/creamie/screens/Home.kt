package com.rajatt7z.creamie.screens

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
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = MaterialTheme.colorScheme.background, // Ensure proper background
        bottomBar = {
            NavigationBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars), // Handle navigation bar
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove default insets since we're handling them manually
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Remove default content insets since we're handling them manually
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) { DashboardScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
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