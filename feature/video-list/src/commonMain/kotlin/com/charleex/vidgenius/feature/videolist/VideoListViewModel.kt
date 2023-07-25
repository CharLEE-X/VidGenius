package com.charleex.vidgenius.feature.videolist

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class VideoListViewModel(
    scope: CoroutineScope,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        VideoListContract.Inputs,
        VideoListContract.Events,
        VideoListContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = VideoListContract.State(),
            inputHandler = VideoListInputHandler(),
            name = "VideoListViewModel",
        )
        .build(),
    eventHandler = VideoListEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(VideoListContract.Inputs.Init)
    }
}
