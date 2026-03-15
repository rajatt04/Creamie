package com.rajatt7z.creamie.presentation.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun PhotoSearchScreen(
    onBack: () -> Unit,
    onPhotoClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Photo Search Screen (Placeholder)")
    }
}

@Composable
fun VideoSearchScreen(
    onBack: () -> Unit,
    onVideoClick: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Video Search Screen (Placeholder)")
    }
}
