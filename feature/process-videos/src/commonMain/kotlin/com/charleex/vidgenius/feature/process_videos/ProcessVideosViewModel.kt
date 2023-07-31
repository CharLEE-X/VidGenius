package com.charleex.vidgenius.feature.process_videos

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ProcessVideosViewModel(
    scope: CoroutineScope,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        ProcessVideosContract.Inputs,
        ProcessVideosContract.Events,
        ProcessVideosContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = ProcessVideosContract.State(),
            inputHandler = ProcessVideosInputHandler(),
            name = "ProcessVideoViewModel",
        )
        .build(),
    eventHandler = ProcessVideosEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(ProcessVideosContract.Inputs.ObserveVideos)
    }
}
