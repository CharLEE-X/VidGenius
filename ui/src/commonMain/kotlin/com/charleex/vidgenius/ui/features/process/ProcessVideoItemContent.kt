package com.charleex.vidgenius.ui.features.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video_item.ProcessVideoItemContract
import com.charleex.vidgenius.feature.process_video_item.ProcessVideoItemViewModel
import com.charleex.vidgenius.feature.process_videos.model.UIProgressState
import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.components.CounterAnimation
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun ProcessVideoItemContent(
    modifier: Modifier = Modifier,
    video: UiVideo,
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    onDeleteClicked: () -> Unit,
    onProcessingStateChanged: (UIProgressState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideoItemViewModel(
            scope = scope,
            uiVideo = video,
            showMessage = displayMessage,
            onProcessingStateChanged = onProcessingStateChanged
        )
    }
    val state by vm.observeStates().collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    val progressState by animateFloatAsState(
        targetValue = when (val uiProgressState = state.uiVideoProcessingState) {
            UIProgressState.Queued,
            UIProgressState.Cancelled,
            is UIProgressState.Error,
            -> 0f

            is UIProgressState.InProgress -> uiProgressState.progress
            UIProgressState.Success -> 1f
        }
    )
    val progressColorState by animateColorAsState(
        targetValue = when (val uiProgressState = state.uiVideoProcessingState) {
            UIProgressState.Queued -> Color.Transparent
            UIProgressState.Cancelled -> Color.Gray
            is UIProgressState.Error -> Color.Red
            is UIProgressState.InProgress -> Color.Blue.copy(alpha = uiProgressState.progress)
            UIProgressState.Success -> Color.Green.copy(alpha = 0.7f)
        }
    )
    val progressTextColorState by animateColorAsState(
        targetValue = when (val uiProgressState = state.uiVideoProcessingState) {
            UIProgressState.Queued -> MaterialTheme.colors.onSurface
            UIProgressState.Cancelled -> Color.Black
            is UIProgressState.Error -> Color.Black
            is UIProgressState.InProgress -> Color.White
            UIProgressState.Success -> Color.Black
        }
    )

//    var dropdownMenuExpanded by remember { mutableStateOf(false) }
//    val categories = listOf(
//        UiVideoCategory(
//            id = "1",
//            name = "Animals",
//        ),
//        UiVideoCategory(
//            id = "2",
//            name = "Cars",
//        ),
//    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(isVisible) {
        AppCard(
            modifier = modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            LinearProgressIndicator(
                progress = progressState,
                color = progressColorState,
                backgroundColor = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .align(Alignment.Center)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 8.dp,
                        horizontal = 64.dp
                    ),
            ) {
                Text(
                    text = state.uiVideo.path,
                    color = progressTextColorState,
                )
                AppFlexSpacer()

                when(val progress = state.uiVideoProcessingState) {
                    is UIProgressState.InProgress -> CounterAnimation(
                            count = (progress.progress * 100).toInt(),
                        ) {
                            Text(
                                text = "$it%",
                                color = progressTextColorState,
                            )
                        }
                    else -> {}
                }

                AnimatedVisibility(
                    visible = state.uiVideoProcessingState !is UIProgressState.InProgress,
                    enter = fadeIn(),
                    exit = fadeOut(),
                )
                {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    vm.trySend(ProcessVideoItemContract.Inputs.Video.CancelProcessingVideo)
                                    onDeleteClicked()
                                }
                            )
                    )
                }
                Spacer(modifier = Modifier.padding(24.dp))
                AnimatedVisibility(
                    visible = state.uiVideoProcessingState == UIProgressState.Queued,
                    enter = fadeIn(),
                    exit = fadeOut(),
                )
                {
                    FloatingActionButton(
                        onClick = { vm.trySend(ProcessVideoItemContract.Inputs.Video.StartVideoProcessing) },
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Video Processing",
                        )
                    }
                }
            }
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//            Text(
//                text = "Category",
//                color = MaterialTheme.colors.onSurface,
//            )
//            OutlinedButton(
//                border = BorderStroke(1.dp, MaterialTheme.colors.primary),
//                onClick = { dropdownMenuExpanded = !dropdownMenuExpanded },
//                modifier = Modifier
//            ) {
//                Text(
//                    text = state.category.name,
//                    color = MaterialTheme.colors.onSurface,
//                )
//            }
//            DropdownMenu(
//                expanded = dropdownMenuExpanded,
//                onDismissRequest = { dropdownMenuExpanded = false },
//                offset = DpOffset(74.dp, 0.dp),
//                modifier = Modifier
//                    .background(MaterialTheme.colors.background)
//            ) {
//                categories.forEach { category ->
//                    DropdownMenuItem(
//                        onClick = {
//                            vm.trySend(
//                                ProcessVideoItemContract.Inputs.Config.SetCategory(category)
//                            )
//                            dropdownMenuExpanded = false
//                        }
//                    ) {
//                        Text(
//                            text = category.name,
//                            color = MaterialTheme.colors.onSurface,
//                        )
//                    }
//                }
//            }
//        }
        }
    }
}
