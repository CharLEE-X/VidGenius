package com.charleex.vidgenius.feature.root

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.SideJobScope
import com.russhwolf.settings.Settings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private typealias RootInputScope = InputHandlerScope<RootContract.Inputs, RootContract.Events, RootContract.State>

internal class RootInputHandler :
    KoinComponent,
    InputHandler<RootContract.Inputs, RootContract.Events, RootContract.State> {

    private val settings by inject<Settings>()

    override suspend fun RootInputScope.handleInput(
        input: RootContract.Inputs,
    ) = when (input) {
        is RootContract.Inputs.Update -> when (input) {
            is RootContract.Inputs.Update.IsAuthenticated ->
                updateState { it.copy(isAuthenticated = input.isAuthenticated) }

            is RootContract.Inputs.Update.IsLoading -> updateState { it.copy(isLoading = input.isLoading) }
        }

        RootContract.Inputs.Init -> init()
        RootContract.Inputs.MonitorAuthState -> monitorAuthState()
    }
}

private suspend fun RootInputScope.init() {
    sideJob("init") {
        postInput(RootContract.Inputs.MonitorAuthState)
    }
}

private suspend fun RootInputScope.monitorAuthState() {
    sideJob("monitorAuthState") {
//        PrintlnLogger().debug("Monitoring auth state")
        postInput(RootContract.Inputs.Update.IsAuthenticated(true))
    }
}
