package com.charleex.vidgenius.feature.videolist

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.charleex.vidgenius.datasource.model.UploadItem

private typealias VideoListInputScope = InputHandlerScope<
        VideoListContract.Inputs,
        VideoListContract.Events,
        VideoListContract.State>

internal class VideoListInputHandler : KoinComponent,
    InputHandler<VideoListContract.Inputs, VideoListContract.Events, VideoListContract.State> {

    private val repository: YoutubeRepository by inject()

    override suspend fun VideoListInputScope.handleInput(
        input: VideoListContract.Inputs,
    ) = when (input) {
        VideoListContract.Inputs.Init -> init()
        VideoListContract.Inputs.GetVideos -> getVideos(repository)
        is VideoListContract.Inputs.Update -> when (input) {
            is VideoListContract.Inputs.Update.SetList -> updateState { it.copy(list = input.list) }
            is VideoListContract.Inputs.Update.ShowLoader -> updateState { it.copy(showLoader = input.showLoading) }
        }
    }
}

private suspend fun VideoListInputScope.init() {
    postInput(VideoListContract.Inputs.GetVideos)
}

private suspend fun VideoListInputScope.getVideos(repository: YoutubeRepository) {
    sideJob("GetVideos") {
        postInput(VideoListContract.Inputs.Update.ShowLoader(true))
        try {
            val channelUploads: List<UploadItem> = repository.getYtChannelUploads()
            val videoListItems = channelUploads.toVideoListItems()
            PrintlnLogger().debug("GetVideos | videoListItems: $videoListItems")
            postInput(VideoListContract.Inputs.Update.SetList(videoListItems))
        } catch (e: Exception) {
            postEvent(
                VideoListContract.Events.ShowError(
                    e.message ?: "Error while getting videos"
                )
            )
        }
        postInput(VideoListContract.Inputs.Update.ShowLoader(false))
        PrintlnLogger().debug("GetVideos | End")
    }
}
