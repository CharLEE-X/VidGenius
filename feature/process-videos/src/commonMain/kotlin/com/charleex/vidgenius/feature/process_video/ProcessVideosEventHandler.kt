package com.charleex.vidgenius.feature.process_video

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class ProcessVideosEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<ProcessVideosContract.Inputs, ProcessVideosContract.Events, ProcessVideosContract.State> {
    override suspend fun EventHandlerScope<
            ProcessVideosContract.Inputs,
            ProcessVideosContract.Events,
            ProcessVideosContract.State>.handleEvent(
        event: ProcessVideosContract.Events,
    ) = when (event) {
        is ProcessVideosContract.Events.ShowError -> showMessage(event.message)
    }
}
