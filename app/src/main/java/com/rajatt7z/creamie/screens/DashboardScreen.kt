package com.rajatt7z.creamie.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rajatt7z.creamie.api.ApiClient
import com.rajatt7z.creamie.api.Photo
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun DashboardScreen() {
    val tabs = listOf("Nature", "Tech", "Games", "Health", "Sea")
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Pexels Dashboard",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open Drawer or Back */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Trigger refresh */ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                },
                windowInsets = WindowInsets(0, 0, 0, 0) // Remove default insets since parent handles them
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Remove default insets since parent handles them
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Tabs linked to pager
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(title) }
                    )
                }
            }

            // Swipeable pages
            HorizontalPager(
                count = tabs.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val query = tabs[page].lowercase()
                var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                // Load photos when each page is visible
                LaunchedEffect(query) {
                    try {
                        isLoading = true
                        val response = ApiClient.api.searchPhotos(query, 15)
                        photos = response.photos
                    } catch (e: Exception) {
                        e.printStackTrace()
                        photos = emptyList()
                    } finally {
                        isLoading = false
                    }
                }

                if (isLoading) {
                    // Show loading spinner
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Show photo list
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(photos) { photo ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.medium,
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(photo.src.medium),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashPreview(){
    DashboardScreen()
}