package com.charleex.vidgenius.feature.dragdrop

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class ProcessVideoEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<ProcessVideoContract.Inputs, ProcessVideoContract.Events, ProcessVideoContract.State> {
    override suspend fun EventHandlerScope<
            ProcessVideoContract.Inputs,
            ProcessVideoContract.Events,
            ProcessVideoContract.State>.handleEvent(
        event: ProcessVideoContract.Events,
    ) = when (event) {
        is ProcessVideoContract.Events.ShowError -> showMessage(event.message)
    }
}
