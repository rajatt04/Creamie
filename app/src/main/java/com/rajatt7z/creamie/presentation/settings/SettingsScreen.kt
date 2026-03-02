package com.rajatt7z.creamie.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SETTINGS",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                scrollBehavior = scrollBehavior
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column {
                    var showThemeDialog by remember { mutableStateOf(false) }
                    SettingsListItem(
                        headline = "Theme",
                        supporting = when (preferences.themeMode) {
                            ThemeMode.LIGHT -> "Light"
                            ThemeMode.DARK -> "Dark"
                            ThemeMode.SYSTEM -> "System Default"
                        },
                        icon = Icons.Outlined.DarkMode,
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.clickable { showThemeDialog = true }
                    )

                    if (showThemeDialog) {
                        AlertDialog(
                            onDismissRequest = { showThemeDialog = false },
                            title = { Text("Choose Theme", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                            text = {
                                Column {
                                    ThemeMode.entries.forEach { mode ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.setThemeMode(mode)
                                                    showThemeDialog = false
                                                }
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = preferences.themeMode == mode,
                                                onClick = {
                                                    viewModel.setThemeMode(mode)
                                                    showThemeDialog = false
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                when (mode) {
                                                    ThemeMode.LIGHT -> "Light"
                                                    ThemeMode.DARK -> "Dark"
                                                    ThemeMode.SYSTEM -> "System Default"
                                                },
                                                style = MaterialTheme.typography.titleMedium
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

            Spacer(modifier = Modifier.height(24.dp))

            // Download & Quality
            SettingsSectionHeader("Downloads")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column {
                    var showQualityDialog by remember { mutableStateOf(false) }
                    SettingsListItem(
                        headline = "Default Quality",
                        supporting = preferences.defaultQuality.replaceFirstChar { it.uppercase() },
                        icon = Icons.Outlined.HighQuality,
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.clickable { showQualityDialog = true }
                    )

                    if (showQualityDialog) {
                        AlertDialog(
                            onDismissRequest = { showQualityDialog = false },
                            title = { Text("Download Quality", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                            text = {
                                Column {
                                    Constants.QUALITY_OPTIONS.forEach { quality ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(16.dp))
                                                .clickable {
                                                    viewModel.setDefaultQuality(quality)
                                                    showQualityDialog = false
                                                }
                                                .padding(vertical = 12.dp, horizontal = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = preferences.defaultQuality == quality,
                                                onClick = {
                                                    viewModel.setDefaultQuality(quality)
                                                    showQualityDialog = false
                                                }
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                quality.replaceFirstChar { it.uppercase() },
                                                style = MaterialTheme.typography.titleMedium
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

            Spacer(modifier = Modifier.height(24.dp))

            // Storage
            SettingsSectionHeader("Storage")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                SettingsListItem(
                    headline = "Clear Cache",
                    supporting = "Clear image and data cache",
                    icon = Icons.Outlined.CleaningServices,
                    iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                    iconTintColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.clickable { viewModel.clearCache() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // API Usage
            SettingsSectionHeader("API Usage")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("This hour", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            "${preferences.apiRequestsUsedThisHour} / ${preferences.apiRateLimitPerHour}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = {
                            (preferences.apiRequestsUsedThisHour.toFloat() / preferences.apiRateLimitPerHour)
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("This month", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
                        Text(
                            "${preferences.totalApiRequestsThisMonth} / ${Constants.RATE_LIMIT_PER_MONTH}",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = {
                            (preferences.totalApiRequestsThisMonth.toFloat() / Constants.RATE_LIMIT_PER_MONTH)
                                .coerceIn(0f, 1f)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About
            SettingsSectionHeader("About")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column {
                    SettingsListItem(
                        headline = "App Version",
                        supporting = "1.0.0",
                        icon = Icons.Outlined.Info,
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsListItem(
                        headline = "Source Code",
                        supporting = "View on GitHub",
                        icon = Icons.Outlined.Code,
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie")
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsListItem(
                        headline = "Privacy Policy",
                        icon = Icons.Outlined.PrivacyTip,
                        iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconTintColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie")
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    SettingsListItem(
                        headline = "Licenses",
                        supporting = "Open source licenses",
                        icon = Icons.Outlined.Description,
                        iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                        iconTintColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.clickable {
                            uriHandler.openUri("https://github.com/rajatt04/Creamie?tab=readme-ov-file#-license")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(130.dp)) // Increased padding for glassy bottom navigation
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(start = 40.dp, end = 24.dp, top = 8.dp, bottom = 12.dp)
    )
}

@Composable
private fun SettingsListItem(
    headline: String,
    supporting: String? = null,
    icon: ImageVector,
    iconBgColor: Color = MaterialTheme.colorScheme.primaryContainer,
    iconTintColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Text(headline, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
        },
        supportingContent = supporting?.let {
            { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTintColor, modifier = Modifier.size(24.dp))
            }
        },
        trailingContent = {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        ),
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    )
}
