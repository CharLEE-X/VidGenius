package com.charleex.vidgenius.ui.features.uploads

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.UploadsManager
import com.charleex.vidgenius.ui.util.pretty
import kotlinx.coroutines.launch

@Composable
fun AnimalsUploadsContent(
    uploadsManager: UploadsManager,
) {
    UploadsContent(
        uploadsManager = uploadsManager,
    )
}

@Composable
fun FailsUploadsContent(
    uploadsManager: UploadsManager,
) {
    UploadsContent(
        uploadsManager = uploadsManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadsContent(
    uploadsManager: UploadsManager,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()
    val scrollbarAdapter = rememberScrollbarAdapter(layColumnState)

    val items by uploadsManager.ytVideos.collectAsState(emptyList())
    val isFetchingUploads by uploadsManager.isFetchingUploads.collectAsState(false)

    var isFirstItemVisible = layColumnState.firstVisibleItemIndex == 0

    val headerElevation by animateDpAsState(
        targetValue = if (isFirstItemVisible) 0.dp else 2.dp,
    )

    val headerTopPadding by animateDpAsState(
        targetValue = if (isFirstItemVisible) 48.dp else 24.dp,
    )

    val headerBottomPadding by animateDpAsState(
        targetValue = if (isFirstItemVisible) 16.dp else 24.dp,
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Surface(
                tonalElevation = headerElevation,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = headerTopPadding,
                            start = 32.dp,
                            end = 64.dp,
                            bottom = headerBottomPadding,
                        )
                ) {
                    Text(
                        text = "Uploads",
                        style = MaterialTheme.typography.headlineLarge,
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    AppFilledButton(
                        label = "Refresh",
                        imageVector = Icons.Default.Refresh,
                        isLoading = isFetchingUploads,
                        onClick = {
                            uploadsManager.fetchUploads()
                        },
                    )
                }
            }
            LazyColumn(
                state = layColumnState,
                contentPadding = PaddingValues(
                    top = 24.dp,
                    bottom = 48.dp,
                    start = 32.dp,
                    end = 64.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(items) {index, item ->
                    Card {
                        ListItem(
                            headlineText = { Text(text = "$index. ${item.title}") },
                            supportingText = { Text(text = item.publishedAt.pretty()) },
                            colors = ListItemDefaults.colors(),
                            tonalElevation = 3.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            !isFirstItemVisible && !isFetchingUploads,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(64.dp)
        ) {
            SmallFloatingActionButton(
                onClick = {
                    scope.launch {
                        layColumnState.animateScrollToItem(0)
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                )
            }
        }

        VerticalScrollbar(
            adapter = scrollbarAdapter,
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 12.dp,
                shape = RoundedCornerShape(8.dp),
                hoverDurationMillis = 300,
                unhoverColor = Color.Black.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.48f),
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}

@Composable
fun AppFilledButton(
    label: String,
    imageVector: ImageVector,
    isLoading: Boolean = false,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .width(200.dp)
            .height(48.dp)
    ) {
        if (!isLoading) {
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
            Text(
                text = label,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        }
    }
}
