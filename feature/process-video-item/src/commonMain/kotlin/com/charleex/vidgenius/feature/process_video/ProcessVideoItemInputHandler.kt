package com.charleex.vidgenius.feature.process_video

import com.charleex.vidgenius.datasource.ProcessingConfig
import com.charleex.vidgenius.datasource.ProcessingState
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.feature.process_video.model.UIProgressState
import com.charleex.vidgenius.feature.process_video.model.toUiProgressState
import com.charleex.vidgenius.feature.process_video.model.toUiVideo
import com.charleex.vidgenius.feature.process_video.model.toVideoCategory
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias ProcessVideoInputScope = InputHandlerScope<
        ProcessVideoItemContract.Inputs,
        ProcessVideoItemContract.Events,
        ProcessVideoItemContract.State>

internal class ProcessVideoItemInputHandler(
    private val videoId: String,
) :
    KoinComponent,
    InputHandler<ProcessVideoItemContract.Inputs, ProcessVideoItemContract.Events, ProcessVideoItemContract.State> {

    private val videoRepository: VideoRepository by inject()
    private val videoProcessing: VideoProcessing by inject()

    override suspend fun ProcessVideoInputScope.handleInput(
        input: ProcessVideoItemContract.Inputs,
    ) = when (input) {
        is ProcessVideoItemContract.Inputs.Video.ObserveUiVideo -> observeVideo(videoId)
        is ProcessVideoItemContract.Inputs.Video.SetUiVideo -> updateState { it.copy(uiVideo = input.uiVideo) }
        is ProcessVideoItemContract.Inputs.Video.StartVideoProcessing -> startProcessingVideo(videoId)
        is ProcessVideoItemContract.Inputs.Video.CancelProcessingVideo -> cancelProcessingVideo(videoId)
        is ProcessVideoItemContract.Inputs.Video.SetVideoProcessingState ->
            updateState { it.copy(uiVideoProcessingState = input.videoProcessingState) }

        is ProcessVideoItemContract.Inputs.Config.SetConfigId -> updateState { it.copy(configId = input.configId) }
        is ProcessVideoItemContract.Inputs.Config.SetChannelId -> updateState { it.copy(channelId = input.channelId) }
        is ProcessVideoItemContract.Inputs.Config.SetNumberOfScreenshots ->
            updateState { it.copy(numberOfScreenshots = input.numberOfScreenshots) }

        is ProcessVideoItemContract.Inputs.Config.SetCategory -> updateState { it.copy(category = input.category) }
        is ProcessVideoItemContract.Inputs.Config.SetUploadYouTube ->
            updateState { it.copy(uploadYouTube = input.uploadYouTube) }

        is ProcessVideoItemContract.Inputs.States.SetVideoProcessingState ->
            updateState { it.copy(videoProcessingState = input.videoProcessingState) }

        is ProcessVideoItemContract.Inputs.States.SetTextProcessingState ->
            updateState { it.copy(textProcessingState = input.textProcessingState) }

        is ProcessVideoItemContract.Inputs.States.SetMetadataGenerationState ->
            updateState { it.copy(metadataGenerationState = input.metadataGenerationState) }

        is ProcessVideoItemContract.Inputs.States.SetUploadYouTubeState ->
            updateState { it.copy(uploadYouTubeState = input.uploadYouTubeState) }
    }

    private fun cancelProcessingVideo(videoId: String) {
        TODO("Not yet implemented")
    }

    private suspend fun ProcessVideoInputScope.observeVideo(videoId: String) {
        sideJob("observeVideo") {
            videoRepository.flowOfVideo(videoId).collect { video ->
                val uiVideo = video.toUiVideo()
                postInput(ProcessVideoItemContract.Inputs.Video.SetUiVideo(uiVideo))
            }
        }
    }

    private suspend fun ProcessVideoInputScope.startProcessingVideo(videoId: String) {
        val state = getCurrentState()
        sideJob("startProcessingVideo") {
            try {
                val config = ProcessingConfig(
                    id = state.configId,
                    channelId = state.channelId,
                    numberOfScreenshots = state.numberOfScreenshots,
                    category = state.category?.toVideoCategory() ?: error("Choose a category"),
                    uploadYouTube = state.uploadYouTube,
                )
                videoProcessing.processVideo(videoId, config).collect { processingState ->
                    postInput(ProcessVideoItemContract.Inputs.Video.SetVideoProcessingState(UIProgressState.InProgress(0F)))
                    when (processingState) {
                        is ProcessingState.VideoProcessing -> {
                            val uiProgressState = processingState.progressState.toUiProgressState()
                            postInput(ProcessVideoItemContract.Inputs.States.SetVideoProcessingState(uiProgressState))
                        }

                        is ProcessingState.TextProcessing -> {
                            val uiProgressState = processingState.progressState.toUiProgressState()
                            postInput(ProcessVideoItemContract.Inputs.States.SetTextProcessingState(uiProgressState))
                        }

                        is ProcessingState.MetadataGeneration -> {
                            val uiProgressState = processingState.progressState.toUiProgressState()
                            postInput(ProcessVideoItemContract.Inputs.States.SetMetadataGenerationState(uiProgressState))
                        }

                        is ProcessingState.UploadVideo.Youtube -> {
                            val uiProgressState = processingState.progressState.toUiProgressState()
                            postInput(ProcessVideoItemContract.Inputs.States.SetUploadYouTubeState(uiProgressState))
                        }

                        ProcessingState.Done -> {
                            postInput(ProcessVideoItemContract.Inputs.Video.SetVideoProcessingState(UIProgressState.Success))
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.message ?: "Error while processing video"
                postEvent(ProcessVideoItemContract.Events.ShowError(message))
                return@sideJob
            }
        }
    }
}
