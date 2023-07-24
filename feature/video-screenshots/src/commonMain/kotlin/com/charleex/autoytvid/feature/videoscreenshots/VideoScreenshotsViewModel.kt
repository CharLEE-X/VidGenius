package com.charleex.autoytvid.feature.videoscreenshots

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class VideoScreenshotsViewModel(
    scope: CoroutineScope,
    filePath: String,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        VideoScreenshotsContract.Inputs,
        VideoScreenshotsContract.Events,
        VideoScreenshotsContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = VideoScreenshotsContract.State(),
            inputHandler = VideoScreenshotsInputHandler(
                filePath = filePath,
            ),
            name = "VideoListViewModel",
        )
        .build(),
    eventHandler = VideoScreenshotsEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(VideoScreenshotsContract.Inputs.Init)
    }
}
