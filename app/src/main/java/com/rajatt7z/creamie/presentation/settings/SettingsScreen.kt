package com.rajatt7z.creamie.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rajatt7z.creamie.core.common.Constants
import com.rajatt7z.creamie.data.local.datastore.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.preferences.collectAsState()
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Appearance
            SettingsSectionHeader("Appearance")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    // Theme mode
                    var showThemeDialog by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text("Theme") },
                        supportingContent = {
                            Text(
                                when (preferences.themeMode) {
                                    ThemeMode.LIGHT -> "Light"
                                    ThemeMode.DARK -> "Dark"
                                    ThemeMode.SYSTEM -> "System Default"
                                }
                            )
                        },
                        leadingContent = { Icon(Icons.Outlined.DarkMode, contentDescription = null) },
                        modifier = Modifier.clickable { showThemeDialog = true }
                    )

                    if (showThemeDialog) {
                        AlertDialog(
                            onDismissRequest = { showThemeDialog = false },
                            title = { Text("Choose Theme") },
                            text = {
                                Column {
                                    ThemeMode.entries.forEach { mode ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.setThemeMode(mode)
                                                    showThemeDialog = false
                                                }
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = preferences.themeMode == mode,
                                                onClick = {
                                                    viewModel.setThemeMode(mode)
                                                    showThemeDialog = false
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                when (mode) {
                                                    ThemeMode.LIGHT -> "Light"
                                                    ThemeMode.DARK -> "Dark"
                                                    ThemeMode.SYSTEM -> "System Default"
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {}
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Download & Quality
            SettingsSectionHeader("Downloads")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    var showQualityDialog by remember { mutableStateOf(false) }
                    ListItem(
                        headlineContent = { Text("Default Quality") },
                        supportingContent = { Text(preferences.defaultQuality.replaceFirstChar { it.uppercase() }) },
                        leadingContent = { Icon(Icons.Outlined.HighQuality, contentDescription = null) },
                        modifier = Modifier.clickable { showQualityDialog = true }
                    )

                    if (showQualityDialog) {
                        AlertDialog(
                            onDismissRequest = { showQualityDialog = false },
                            title = { Text("Default Download Quality") },
                            text = {
                                Column {
                                    Constants.QUALITY_OPTIONS.forEach { quality ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.setDefaultQuality(quality)
                                                    showQualityDialog = false
                                                }
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = preferences.defaultQuality == quality,
                                                onClick = {
                                                    viewModel.setDefaultQuality(quality)
                                                    showQualityDialog = false
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(quality.replaceFirstChar { it.uppercase() })
                                        }
                                    }
                                }
                            },
                            confirmButton = {}
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Storage
            SettingsSectionHeader("Storage")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                ListItem(
                    headlineContent = { Text("Clear Cache") },
                    supportingContent = { Text("Clear image and data cache") },
                    leadingContent = { Icon(Icons.Outlined.CleaningServices, contentDescription = null) },
                    modifier = Modifier.clickable { viewModel.clearCache() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // API Usage
            SettingsSectionHeader("API Usage")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("This hour", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${preferences.apiRequestsUsedThisHour} / ${preferences.apiRateLimitPerHour}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            (preferences.apiRequestsUsedThisHour.toFloat() / preferences.apiRateLimitPerHour)
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("This month", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${preferences.totalApiRequestsThisMonth} / ${Constants.RATE_LIMIT_PER_MONTH}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            (preferences.totalApiRequestsThisMonth.toFloat() / Constants.RATE_LIMIT_PER_MONTH)
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // About
            SettingsSectionHeader("About")
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("App Version") },
                        supportingContent = { Text("1.0.0") },
                        leadingContent = { Icon(Icons.Outlined.Info, contentDescription = null) }
                    )
                    ListItem(
                        headlineContent = { Text("Source Code") },
                        supportingContent = { Text("View on GitHub") },
                        leadingContent = { Icon(Icons.Outlined.Code, contentDescription = null) },
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie")
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Privacy Policy") },
                        leadingContent = { Icon(Icons.Outlined.PrivacyTip, contentDescription = null) },
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie")
                        }
                    )
                    ListItem(
                        headlineContent = { Text("Licenses") },
                        supportingContent = { Text("Open source licenses") },
                        leadingContent = { Icon(Icons.Outlined.Description, contentDescription = null) },
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie?tab=readme-ov-file#-license")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
