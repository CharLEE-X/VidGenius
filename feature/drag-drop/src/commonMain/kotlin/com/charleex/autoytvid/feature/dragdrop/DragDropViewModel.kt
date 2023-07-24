package com.charleex.autoytvid.feature.dragdrop

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class DragDropViewModel(
    scope: CoroutineScope,
    showMessage: (String) -> Unit,
) : BasicViewModel<
        DragDropContract.Inputs,
        DragDropContract.Events,
        DragDropContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = DragDropContract.State(),
            inputHandler = DragDropInputHandler(),
            name = "VideoListViewModel",
        )
        .build(),
    eventHandler = DragDropEventHandler(
        showMessage = showMessage,
    ),
    coroutineScope = scope,
)
