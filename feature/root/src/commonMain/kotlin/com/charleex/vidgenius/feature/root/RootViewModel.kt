package com.charleex.vidgenius.feature.root

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class RootViewModel(
    scope: CoroutineScope,
) : BasicViewModel<
        RootContract.Inputs,
        RootContract.Events,
        RootContract.State>(
    coroutineScope = scope,
    config = BallastViewModelConfiguration.Builder()
        .apply {
            this += LoggingInterceptor()
        }
        .withViewModel(
            initialState = RootContract.State(),
            inputHandler = RootInputHandler(),
            name = TAG,
        )
        .build(),
    eventHandler = RootEventHandler(),
) {
    init {
        trySend(RootContract.Inputs.Init)
    }

    companion object {
        const val TAG = "RootViewModel"
    }
}
