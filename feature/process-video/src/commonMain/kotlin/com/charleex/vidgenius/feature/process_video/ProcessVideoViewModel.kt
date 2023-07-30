package com.charleex.vidgenius.feature.process_video

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ProcessVideoViewModel(
    scope: CoroutineScope,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        ProcessVideoContract.Inputs,
        ProcessVideoContract.Events,
        ProcessVideoContract.State>(
    config = BallastViewModelConfiguration.Builder()
        .apply {
            this += LoggingInterceptor()
            logger = { PrintlnLogger() }
        }
        .withViewModel(
            initialState = ProcessVideoContract.State(),
            inputHandler = ProcessVideoInputHandler(),
            name = "DragDropViewModel",
        )
        .build(),
    eventHandler = ProcessVideoEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(ProcessVideoContract.Inputs.Video.ObserveUiVideo)
    }
}
