package com.charleex.vidgenius.ui.features.process.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoViewModel
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.charleex.vidgenius.ui.features.process.components.ArrowDown
import com.charleex.vidgenius.ui.features.process.components.SectionContainer

@Composable
internal fun ColumnScope.DescriptionContent(
    modifier: Modifier = Modifier,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {
    AnimatedVisibility(state.descriptionState !is ProgressState.None) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArrowDown(progressState = state.descriptionState)
            SectionContainer(
                name = "Description",
                progressState = state.descriptionState,
                isOpen = state.isDescriptionOpen,
                onOpenClicked = { vm.trySend(ProcessVideoContract.Inputs.Description.ToggleIsOpen) },
                enabled = state.descriptionState is ProgressState.Success,
                modifier = modifier
            ) {
                Column(
                    modifier = Modifier.padding(
                        vertical = 32.dp,
                        horizontal = 32.dp
                    )
                ) {
                    state.uiVideo?.screenshots?.forEachIndexed { index, uiScreenshot ->
                        Row {
                            Text(
                                text = "${index + 1}. ",
                                modifier = Modifier.padding(8.dp)
                            )
                            uiScreenshot.description?.let { description ->
                                Text(
                                    text = description,
                                    modifier = Modifier.padding(8.dp)
                                )
                            } ?: Text(
                                text = "No description",
                                color = MaterialTheme.colors.onBackground,
                            )
                        }
                    }
                }
            }
        }
    }
}

