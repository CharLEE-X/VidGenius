package com.charleex.vidgenius.feature.root

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger


internal class RootEventHandler() : EventHandler<RootContract.Inputs, RootContract.Events, RootContract.State> {
    override suspend fun EventHandlerScope<RootContract.Inputs, RootContract.Events, RootContract.State>.handleEvent(
        event: RootContract.Events,
    ) = when (event) {
        is RootContract.Events.Authenticated -> PrintlnLogger().info("AUTHENTICATED")
        RootContract.Events.NotAuthenticated -> {
            PrintlnLogger().info("NOT AUTHENTICATED -> Redirecting to login page.")
        }
    }
}
