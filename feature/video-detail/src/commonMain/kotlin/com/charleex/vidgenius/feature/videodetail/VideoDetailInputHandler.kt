package com.charleex.vidgenius.feature.videodetail

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.postInput
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import src.charleex.vidgenius.repository.YoutubeRepository

private typealias VideoDetailInputScope = InputHandlerScope<
        VideoDetailContract.Inputs,
        VideoDetailContract.Events,
        VideoDetailContract.State>

internal class VideoDetailInputHandler :
    KoinComponent,
    InputHandler<VideoDetailContract.Inputs, VideoDetailContract.Events, VideoDetailContract.State> {

    private val repository: YoutubeRepository by inject()

    override suspend fun VideoDetailInputScope.handleInput(
        input: VideoDetailContract.Inputs,
    ) = when (input) {
        is VideoDetailContract.Inputs.Init -> init(input.videoId)
        is VideoDetailContract.Inputs.GetVideoDetail -> getVideos(input.videoId, repository)
        is VideoDetailContract.Inputs.Update -> when (input) {
            is VideoDetailContract.Inputs.Update.SetDetail -> updateState { it.copy(videoDetail = input.videoDetail) }
            is VideoDetailContract.Inputs.Update.SetLoading -> updateState { it.copy(loading = input.loading) }
        }
    }

    private suspend fun VideoDetailInputScope.init(videoId: String) {
        sideJob("GetVideo $videoId") {
            PrintlnLogger().debug("GetVideo | $videoId | Start")
            postInput(VideoDetailContract.Inputs.Update.SetLoading(true))
            postInput(VideoDetailContract.Inputs.GetVideoDetail(videoId))
            postInput(VideoDetailContract.Inputs.Update.SetLoading(false))
            PrintlnLogger().debug("GetVideo | $videoId | End")
        }
    }
}


private suspend fun VideoDetailInputScope.getVideos(videoId: String, repository: YoutubeRepository) {
    try {
        val uploadItem = repository.getVideoDetail(videoId)
        val videoDetail = uploadItem.toVideoDetail()
        postInput(VideoDetailContract.Inputs.Update.SetDetail(videoDetail))
    } catch (e: Exception) {
        postEvent(
            VideoDetailContract.Events.ShowError(
                e.message ?: "Error while getting video $videoId"
            )
        )
    }
}

