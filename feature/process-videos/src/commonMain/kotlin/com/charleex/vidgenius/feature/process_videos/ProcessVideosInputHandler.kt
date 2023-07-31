package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.feature.process_videos.model.toUiVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias ProcessVideoInputScope = InputHandlerScope<
        ProcessVideosContract.Inputs,
        ProcessVideosContract.Events,
        ProcessVideosContract.State>

internal class ProcessVideosInputHandler :
    KoinComponent,
    InputHandler<ProcessVideosContract.Inputs, ProcessVideosContract.Events, ProcessVideosContract.State> {

    private val videoRepository: VideoRepository by inject()

    override suspend fun ProcessVideoInputScope.handleInput(
        input: ProcessVideosContract.Inputs,
    ) = when (input) {
        ProcessVideosContract.Inputs.ObserveVideos -> observeVideos()
        is ProcessVideosContract.Inputs.SetVideos -> updateState { it.copy(videos = input.videos) }
        is ProcessVideosContract.Inputs.DeleteVideoId -> deleteVideo(input.videoId, videoRepository)
        is ProcessVideosContract.Inputs.HandleFiles -> handleFiles(input.files, videoRepository)
    }

    private suspend fun ProcessVideoInputScope.observeVideos() {
        sideJob("observeVideosIds") {
            videoRepository.flowOfVideos().collect { videos ->
                val uiVideos = videos.map { it.toUiVideo() }
                postInput(ProcessVideosContract.Inputs.SetVideos(videos = uiVideos))
            }
        }
    }

    private fun ProcessVideoInputScope.deleteVideo(videoId: String, videoRepository: VideoRepository) {
        sideJob("deleteVideo") {
            videoRepository.deleteVideo(videoId)
        }
    }

    private suspend fun ProcessVideoInputScope.handleFiles(files: List<*>, videoRepository: VideoRepository) {
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
}
