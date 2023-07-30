package com.charleex.vidgenius.ui.features.process.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
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
internal fun MetaDataContent(
    modifier: Modifier = Modifier,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {
    AnimatedVisibility(state.metaDataState !is ProgressState.None) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArrowDown(progressState = state.metaDataState)
            SectionContainer(
                name = "MetaData",
                progressState = state.metaDataState,
                isOpen = state.isMetaDataOpen,
                onOpenClicked = { vm.trySend(ProcessVideoContract.Inputs.MetaData.ToggleIsOpen) },
                enabled = state.metaDataState is ProgressState.Success,
                modifier = modifier
            ) {
                state.uiVideo?.let { uiVideo ->
                    Column(
                        modifier = Modifier.padding(
                            vertical = 32.dp,
                            horizontal = 32.dp
                        )
                    ) {
                        uiVideo.title?.let { title ->
                            Text(
                                text = title,
                                color = MaterialTheme.colors.onBackground,
                            )
                        } ?: Text(
                            text = "No title",
                            color = MaterialTheme.colors.onBackground,
                        )
                        uiVideo.description?.let { description ->
                            Text(
                                text = description,
                                color = MaterialTheme.colors.onBackground,
                            )
                        } ?: Text(
                            text = "No description",
                            color = MaterialTheme.colors.onBackground,
                        )
                        if (uiVideo.tags.isNotEmpty()) {
                            Text(
                                text = "Tags",
                                color = MaterialTheme.colors.onBackground,
                            )
                        } else {
                            Text(
                                text = "No tags",
                                color = MaterialTheme.colors.onBackground,
                            )
                        }
                    }
                } ?: Text(
                    text = "No video",
                    color = MaterialTheme.colors.onBackground,
                )
            }
        }
    }
}
