package com.charleex.vidgenius.ui.features.process.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoViewModel
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.charleex.vidgenius.feature.process_video.model.UiScreenshot
import com.charleex.vidgenius.ui.components.ImageFromBufferedImage
import com.charleex.vidgenius.ui.features.process.components.ArrowDown
import com.charleex.vidgenius.ui.features.process.components.SectionContainer

@Composable
internal fun ColumnScope.ScreenshotsContent(
    modifier: Modifier = Modifier,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {

    AnimatedVisibility(state.screenshotsState !is ProgressState.None) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArrowDown(progressState = state.screenshotsState)
            SectionContainer(
                name = "Screenshots",
                progressState = state.screenshotsState,
                isOpen = state.isScreenshotsOpen,
                onOpenClicked = { vm.trySend(ProcessVideoContract.Inputs.Screenshots.ToggleIsOpen) },
                enabled = state.screenshotsState is ProgressState.Success,
                modifier = modifier
            ) {
                Screenshots(
                    vm = vm,
                    state = state,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun Screenshots(
    modifier: Modifier = Modifier,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        state.uiVideo?.let {
            if (it.screenshots.isEmpty()) {
                Text(
                    text = "No screenshots yet",
                    modifier = Modifier.padding(32.dp)
                )
            } else {
                it.screenshots.forEach { screenshot ->
                    ScreenshotItem(
                        vm = vm,
                        screenshot = screenshot,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenshotItem(
    modifier: Modifier,
    vm: ProcessVideoViewModel,
    screenshot: UiScreenshot,
    size: Int = 100
) {
    val interactionSource = MutableInteractionSource()
    var isZoomed by remember { mutableStateOf(false) }
    val scaleState by animateFloatAsState(
        targetValue = if (isZoomed) 2f else 1f,
        animationSpec = tween(100)
    )
    val translationYState by animateFloatAsState(
        targetValue = if (isZoomed) -(size / 3).toFloat() else 0f,
        animationSpec = tween(100)
    )

    Surface(
        shape = RoundedCornerShape(10.dp),
        elevation = 0.dp,
        modifier = modifier
            .size(size.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    isZoomed = !isZoomed
                }
            )
            .graphicsLayer {
                scaleX = scaleState
                scaleY = scaleState
                translationY = translationYState
            }
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            ImageFromBufferedImage(
                filePath = screenshot.path,
                onError = { screenshotPath ->
                    vm.trySend(ProcessVideoContract.Inputs.Screenshots.DeleteScreenshot(screenshotPath))
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
