package com.charleex.vidgenius.feature.videoscreenshots

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ScreenshotsToTextViewModel(
    scope: CoroutineScope,
    videoId: String,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        ScreenshotsToTextContract.Inputs,
        ScreenshotsToTextContract.Events,
        ScreenshotsToTextContract.State>(
    config = BallastViewModelConfiguration.Builder()
        .apply {
            this += LoggingInterceptor()
            logger = { PrintlnLogger() }
        }
        .withViewModel(
            initialState = ScreenshotsToTextContract.State(),
            inputHandler = ScreenshotsToTextInputHandler(
                videoId = videoId,
            ),
            name = "ScreenshotsToTextViewModel",
        )
        .build(),
    eventHandler = ScreenshotsToTextEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(ScreenshotsToTextContract.Inputs.Init)
    }
}
