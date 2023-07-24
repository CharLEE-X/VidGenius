package com.charleex.autoytvid.feature.videodetail

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class DragDropEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<DragDropContract.Inputs, DragDropContract.Events, DragDropContract.State> {
    override suspend fun EventHandlerScope<
            DragDropContract.Inputs,
            DragDropContract.Events,
            DragDropContract.State>.handleEvent(
        event: DragDropContract.Events,
    ) = when (event) {
        is DragDropContract.Events.ShowError -> showMessage(event.message)
    }
}
