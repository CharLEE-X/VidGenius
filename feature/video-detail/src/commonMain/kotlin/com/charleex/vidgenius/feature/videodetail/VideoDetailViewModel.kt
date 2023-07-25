package com.charleex.vidgenius.feature.videodetail

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class VideoDetailViewModel(
    scope: CoroutineScope,
    videoId: String,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        VideoDetailContract.Inputs,
        VideoDetailContract.Events,
        VideoDetailContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = VideoDetailContract.State(),
            inputHandler = VideoDetailInputHandler(),
            name = "VideoListViewModel",
        )
        .build(),
    eventHandler = VideoDetailEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
) {
    init {
        trySend(VideoDetailContract.Inputs.Init(videoId))
    }
}
