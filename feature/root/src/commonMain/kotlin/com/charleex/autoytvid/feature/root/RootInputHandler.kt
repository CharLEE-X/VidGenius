package com.charleex.autoytvid.feature.root

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent

private typealias RootInputScope = InputHandlerScope<RootContract.Inputs, RootContract.Events, RootContract.State>

internal class RootInputHandler :
    KoinComponent,
    InputHandler<RootContract.Inputs, RootContract.Events, RootContract.State> {

    override suspend fun RootInputScope.handleInput(
        input: RootContract.Inputs,
    ) = when (input) {
        is RootContract.Inputs.Update.IsAuthenticated ->
            updateState { it.copy(isAuthenticated = input.isAuthenticated) }
        is RootContract.Inputs.Update.IsLoading ->
            updateState { it.copy(isLoading = input.isLoading) }

        RootContract.Inputs.Init -> init()
        RootContract.Inputs.MonitorAuthState -> monitorAuthState()
    }
}

private suspend fun RootInputScope.init() {
    postInput(RootContract.Inputs.MonitorAuthState)
}

private suspend fun RootInputScope.monitorAuthState() {
    postInput(RootContract.Inputs.Update.IsAuthenticated(true))
}
