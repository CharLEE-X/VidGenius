package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.feature.process_videos.model.toUiVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias ProcessVideosInputScope = InputHandlerScope<
        ProcessVideosContract.Inputs,
        ProcessVideosContract.Events,
        ProcessVideosContract.State>

internal class ProcessVideosInputHandler :
    KoinComponent,
    InputHandler<ProcessVideosContract.Inputs, ProcessVideosContract.Events, ProcessVideosContract.State> {

    private val videoRepository: VideoRepository by inject()
    private val videoProcessing: VideoProcessing by inject()

    override suspend fun ProcessVideosInputScope.handleInput(
        input: ProcessVideosContract.Inputs,
    ) = when (input) {
        ProcessVideosContract.Inputs.ObserveVideos -> observeVideos()
        is ProcessVideosContract.Inputs.SetVideos -> updateState { it.copy(videos = input.videos) }
        is ProcessVideosContract.Inputs.DeleteVideoId -> deleteVideo(input.videoId, videoRepository)
        is ProcessVideosContract.Inputs.HandleFiles -> handleFiles(input.files, videoRepository)

        is ProcessVideosContract.Inputs.StartVideoProcessing -> startProcessingVideo(input.videoId)
        is ProcessVideosContract.Inputs.CancelProcessingVideo -> cancelProcessingVideo(input.videoId)

        is ProcessVideosContract.Inputs.SetCategory -> updateState { it.copy(category = input.category) }
        is ProcessVideosContract.Inputs.SetChannelId -> updateState { it.copy(channelId = input.channelId) }
        is ProcessVideosContract.Inputs.SetNumberOfScreenshots -> updateState { it.copy(numberOfScreenshots = input.numberOfScreenshots) }
        is ProcessVideosContract.Inputs.SetUploadYouTube -> updateState { it.copy(uploadYouTube = input.uploadYouTube) }

        is ProcessVideosContract.Inputs.SetQueue -> updateState { it.copy(queue = input.queue) }
        is ProcessVideosContract.Inputs.SetProgress -> updateState { it.copy(progress = input.progress) }
        is ProcessVideosContract.Inputs.SetDone -> updateState { it.copy(done = input.done) }
        is ProcessVideosContract.Inputs.SetCanceled -> updateState { it.copy(canceled = input.canceled) }
        is ProcessVideosContract.Inputs.SetFailed -> updateState { it.copy(failed = input.failed) }
    }

    private suspend fun ProcessVideosInputScope.observeVideos() {
        sideJob("observeVideosIds") {
            videoRepository.flowOfVideos().collect { videos ->
                val uiVideos = videos.map { it.toUiVideo() }
                postInput(ProcessVideosContract.Inputs.SetVideos(videos = uiVideos))
            }
        }
    }

    private fun ProcessVideosInputScope.deleteVideo(
        videoId: String,
        videoRepository: VideoRepository,
    ) {
        sideJob("deleteVideo") {
            videoRepository.deleteVideo(videoId)
        }
    }

    private suspend fun ProcessVideosInputScope.handleFiles(
        files: List<*>,
        videoRepository: VideoRepository,
    ) {
        sideJob("handleFiles") {
            try {
                videoRepository.filterVideos(files)
            } catch (e: Exception) {
                e.printStackTrace()
                val message = e.message ?: "Error while getting files"
                postEvent(ProcessVideosContract.Events.ShowError(message))
                return@sideJob
            }
        }
    }

    private fun ProcessVideosInputScope.cancelProcessingVideo(videoId: String) {
        cancelSideJob("processing-$videoId")
    }

    private suspend fun ProcessVideosInputScope.startProcessingVideo(videoId: String) {
        val state = getCurrentState()
        sideJob("processing-$videoId") {
            videoProcessing.processAndUploadVideo(
                videoId = videoId,
                channelId = state.channelId,
                numberOfScreenshots = state.numberOfScreenshots,
                category = state.category.name,
                uploadYouTube = state.uploadYouTube,

                )
        }
    }
}
