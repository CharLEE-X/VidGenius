package com.charleex.vidgenius.feature.videodetail

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class VideoDetailEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<VideoDetailContract.Inputs, VideoDetailContract.Events, VideoDetailContract.State> {
    override suspend fun EventHandlerScope<
            VideoDetailContract.Inputs,
            VideoDetailContract.Events,
            VideoDetailContract.State>.handleEvent(
        event: VideoDetailContract.Events,
    ) = when (event) {
        is VideoDetailContract.Events.ShowError -> showMessage(event.message)
    }
}
