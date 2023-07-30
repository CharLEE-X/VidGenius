package com.charleex.vidgenius.ui.features.process.section

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charleex.vidgenius.feature.process_video.ProcessVideoContract
import com.charleex.vidgenius.feature.process_video.ProcessVideoViewModel
import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.charleex.vidgenius.ui.features.process.components.ArrowDown
import com.charleex.vidgenius.ui.features.process.components.SectionContainer

@Composable
internal fun UploadContent(
    modifier: Modifier = Modifier,
    vm: ProcessVideoViewModel,
    state: ProcessVideoContract.State,
) {
    AnimatedVisibility(state.uploadState !is ProgressState.None) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ArrowDown(progressState = state.uploadState)
            SectionContainer(
                name = "Upload",
                progressState = state.uploadState,
                isOpen = state.isUploadOpen,
                onOpenClicked = { vm.trySend(ProcessVideoContract.Inputs.Upload.ToggleIsOpen) },
                enabled = state.uploadState is ProgressState.Success || state.uploadState is ProgressState.Error,
                modifier = modifier
            ) {
                Text(
                    text = "Upload",
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.h6,
                )
            }
        }
    }
}

