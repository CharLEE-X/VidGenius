package com.charleex.vidgenius.feature.process_video

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class ProcessVideoItemEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<ProcessVideoItemContract.Inputs, ProcessVideoItemContract.Events, ProcessVideoItemContract.State> {
    override suspend fun EventHandlerScope<
            ProcessVideoItemContract.Inputs,
            ProcessVideoItemContract.Events,
            ProcessVideoItemContract.State>.handleEvent(
        event: ProcessVideoItemContract.Events,
    ) = when (event) {
        is ProcessVideoItemContract.Events.ShowError -> showMessage(event.message)
    }
}
