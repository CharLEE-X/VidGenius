package com.charleex.vidgenius.feature.videoscreenshots

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class ScreenshotsToTextEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<ScreenshotsToTextContract.Inputs, ScreenshotsToTextContract.Events, ScreenshotsToTextContract.State> {
    override suspend fun EventHandlerScope<
            ScreenshotsToTextContract.Inputs,
            ScreenshotsToTextContract.Events,
            ScreenshotsToTextContract.State>.handleEvent(
        event: ScreenshotsToTextContract.Events,
    ) = when (event) {
        is ScreenshotsToTextContract.Events.ShowError -> showMessage(event.message)
    }
}
