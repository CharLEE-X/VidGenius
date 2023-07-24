package com.charleex.autoytvid.feature.videolist

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class VideoListEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<VideoListContract.Inputs, VideoListContract.Events, VideoListContract.State> {
    override suspend fun EventHandlerScope<
            VideoListContract.Inputs,
            VideoListContract.Events,
            VideoListContract.State>.handleEvent(
        event: VideoListContract.Events,
    ) = when (event) {
        is VideoListContract.Events.ShowError -> showMessage(event.message)
    }
}
