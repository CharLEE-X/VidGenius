package com.charleex.vidgenius.feature.videoscreenshots

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

internal class VideoScreenshotsEventHandler(
    private val showMessage: (String) -> Unit,
) :
    EventHandler<VideoScreenshotsContract.Inputs, VideoScreenshotsContract.Events, VideoScreenshotsContract.State> {
    override suspend fun EventHandlerScope<
            VideoScreenshotsContract.Inputs,
            VideoScreenshotsContract.Events,
            VideoScreenshotsContract.State>.handleEvent(
        event: VideoScreenshotsContract.Events,
    ) = when (event) {
        is VideoScreenshotsContract.Events.ShowError -> showMessage(event.message)
    }
}
