package com.charleex.vidgenius.feature.process_video_item

import com.charleex.vidgenius.datasource.ProcessingConfig
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.feature.process_videos.model.toVideoCategory
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias ProcessVideoItemInputScope = InputHandlerScope<
        ProcessVideoItemContract.Inputs,
        ProcessVideoItemContract.Events,
        ProcessVideoItemContract.State>

internal class ProcessVideoItemInputHandler :
    KoinComponent,
    InputHandler<ProcessVideoItemContract.Inputs, ProcessVideoItemContract.Events, ProcessVideoItemContract.State> {

    private val videoProcessing: VideoProcessing by inject()

    override suspend fun ProcessVideoItemInputScope.handleInput(
        input: ProcessVideoItemContract.Inputs,
    ) = when (input) {
        is ProcessVideoItemContract.Inputs.Video.StartVideoProcessing -> startProcessingVideo()
        is ProcessVideoItemContract.Inputs.Video.CancelProcessingVideo -> cancelProcessingVideo()

        is ProcessVideoItemContract.Inputs.Config.SetConfigId -> updateState { it.copy(configId = input.configId) }
        is ProcessVideoItemContract.Inputs.Config.SetChannelId -> updateState { it.copy(channelId = input.channelId) }
        is ProcessVideoItemContract.Inputs.Config.SetNumberOfScreenshots ->
            updateState { it.copy(numberOfScreenshots = input.numberOfScreenshots) }

        is ProcessVideoItemContract.Inputs.Config.SetCategory -> updateState { it.copy(category = input.category) }
        is ProcessVideoItemContract.Inputs.Config.SetUploadYouTube ->
            updateState { it.copy(uploadYouTube = input.uploadYouTube) }
    }

    private fun ProcessVideoItemInputScope.cancelProcessingVideo() {
        cancelSideJob("startProcessingVideo")
    }

    private suspend fun ProcessVideoItemInputScope.startProcessingVideo() {
        val state = getCurrentState()
        sideJob("startProcessingVideo") {
            val config = ProcessingConfig(
                id = state.configId,
                channelId = state.channelId,
                numberOfScreenshots = state.numberOfScreenshots,
                category = state.category.toVideoCategory(),
                uploadYouTube = state.uploadYouTube,
            )
            videoProcessing.processAndUploadVideo(state.uiVideo.id, config)
        }
    }
}
