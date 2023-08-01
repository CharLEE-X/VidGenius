package com.charleex.vidgenius.feature.process_video_item

import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ProcessVideoItemViewModel(
    scope: CoroutineScope,
    uiVideo: UiVideo,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        ProcessVideoItemContract.Inputs,
        ProcessVideoItemContract.Events,
        ProcessVideoItemContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = ProcessVideoItemContract.State(
                uiVideo = uiVideo,
            ),
            inputHandler = ProcessVideoItemInputHandler(),
            name = "ProcessVideoItemViewModel",
        )
        .build(),
    eventHandler = ProcessVideoItemEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
)
