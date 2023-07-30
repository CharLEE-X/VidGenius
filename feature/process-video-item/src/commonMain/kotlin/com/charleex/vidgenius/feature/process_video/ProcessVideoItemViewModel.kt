package com.charleex.vidgenius.feature.process_video

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ProcessVideoItemViewModel(
    scope: CoroutineScope,
    videoId: String,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        ProcessVideoItemContract.Inputs,
        ProcessVideoItemContract.Events,
        ProcessVideoItemContract.State>(
    config = BallastViewModelConfiguration.Builder()
        .apply {
            this += LoggingInterceptor()
            logger = { PrintlnLogger() }
        }
        .withViewModel(
            initialState = ProcessVideoItemContract.State(),
            inputHandler = ProcessVideoItemInputHandler(videoId),
            name = "ProcessVideoViewModel",
        )
        .build(),
    eventHandler = ProcessVideoItemEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(ProcessVideoItemContract.Inputs.Video.ObserveUiVideo)
    }
}
