package com.rajatt7z.creamie.presentation.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rajatt7z.creamie.domain.model.Collection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    onCollectionClick: (String, String) -> Unit, // id, title
    viewModel: CollectionsViewModel = hiltViewModel()
) {
    val items = viewModel.collections.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Featured Collections", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp, 
                end = 16.dp, 
                top = padding.calculateTopPadding(), 
                bottom = 120.dp
            ),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (items.loadState.refresh is LoadState.Loading) {
                items(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    )
                }
            }

            items(count = items.itemCount) { index ->
                val collection = items[index]
                if (collection != null) {
                    CollectionCard(
                        collection = collection,
                        index = index,
                        onClick = { onCollectionClick(collection.id, collection.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: Collection,
    index: Int,
    onClick: () -> Unit
) {
    // Generate a consistent but varied gradient color based on the index
    val hue = (index * 137.5f) % 360f // Golden angle approximation for distribution
    val topColor = Color.hsv(hue, 0.6f, 0.8f)
    val bottomColor = Color.hsv((hue + 40f) % 360f, 0.8f, 0.6f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "PEXELS",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White.copy(alpha = 0.7f)
            )

            Column {
                Text(
                    collection.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${collection.mediaCount} Media",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "View \u2192",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
