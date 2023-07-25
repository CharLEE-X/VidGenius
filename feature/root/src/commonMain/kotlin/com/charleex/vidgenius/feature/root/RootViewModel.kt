package com.charleex.vidgenius.feature.root

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class RootViewModel(
    scope: CoroutineScope,
) : BasicViewModel<
        RootContract.Inputs,
        RootContract.Events,
        RootContract.State>(
    config = BallastViewModelConfiguration.Builder()
//        .apply {
//            this += LoggingInterceptor()
//            logger = { PrintlnLogger() }
//        }
        .withViewModel(
            initialState = RootContract.State(),
            inputHandler = RootInputHandler(),
            name = "LoginViewModel",
        )
        .build(),
    eventHandler = RootEventHandler(),
    coroutineScope = scope,
) {
    init {
        trySend(RootContract.Inputs.Init)
    }
}
