package com.charleex.vidgenius.ui.features.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.Icon
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.dragdrop.ProcessVideoViewModel
import com.charleex.vidgenius.feature.dragdrop.model.ProgressState
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.features.process.section.DragContent
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun DragDropContent(
    breakpoint: Breakpoint,
    displayMessage: (message: String) -> Unit,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val vm = remember(scope) {
        ProcessVideoViewModel(
            scope = scope,
            showMessage = displayMessage,
        )
    }
    val state by vm.observeStates().collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp)
        ) {
            DragContent(
                window = window,
                vm = vm,
                state = state,
                modifier = Modifier
            )
        }
    }
}

@Composable
internal fun SectionContainer(
    modifier: Modifier = Modifier,
    name: String,
    progressState: ProgressState,
    isOpenInitially: Boolean = false,
    block: @Composable ColumnScope.() -> Unit,
) {
    val indicationSource = remember { MutableInteractionSource() }
    var isOpen by remember { mutableStateOf(isOpenInitially) }
    val headerBgColor by animateColorAsState(
        targetValue = when (isOpen) {
            true -> MaterialTheme.colors.background.copy(alpha = 0.3f)
            false -> MaterialTheme.colors.surface
        }
    )

    AppCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            SectionHeader(
                name = name,
                progressState = progressState,
                bgColor = headerBgColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        indication = null,
                        interactionSource = indicationSource,
                        onClick = { isOpen = !isOpen }
                    )
            )
            AnimatedVisibility(isOpen) {
                block()
            }
        }
    }
}

@Composable
private fun SectionHeader(
    modifier: Modifier = Modifier,
    name: String,
    progressState: ProgressState,
    bgColor: Color,
) {
    Surface(
        color = bgColor,
        elevation = 0.dp,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 20.dp,
                    horizontal = 48.dp
                )
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.onSurface,
            )
            AppFlexSpacer()
            when (progressState) {
                ProgressState.None -> {
                    // No-op
                }

                ProgressState.Queued -> {
                    Text(
                        text = "Queued",
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                    )
                }

                is ProgressState.Loading -> {
                    LinearProgressIndicator(
                        progress = progressState.progress,
                        modifier = Modifier
                            .fillMaxWidth(.8f)
                    )
                }

                is ProgressState.Success -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(48.dp),
                    ) {
                        Text(
                            text = progressState.message ?: "Success",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.Green,
                            modifier = Modifier
                        )
                    }
                }

                is ProgressState.Error -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(48.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Failed: ${progressState.message}",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onSurface,
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Success",
                            tint = Color.Red,
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
