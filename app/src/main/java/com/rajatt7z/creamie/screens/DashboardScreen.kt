package com.rajatt7z.creamie.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rajatt7z.creamie.api.ApiClient
import com.rajatt7z.creamie.api.Photo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    var photos by remember { mutableStateOf<List<Photo>>(emptyList()) }

    // Load photos on launch
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.api.searchPhotos("nature", 15)
            photos = response.photos
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Pexels Dashboard") }) }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(12.dp)
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

@Preview(showBackground = true)
@Composable
fun DashPreview() {
    DashboardScreen()
}
