package com.charleex.vidgenius.feature.process_video

import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import kotlinx.coroutines.flow.first
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
        ProcessVideosContract.Inputs.ObserveVideosIds -> observeVideosIds()
        is ProcessVideosContract.Inputs.SetVideosIds -> updateState { it.copy(videos = input.videos) }
        is ProcessVideosContract.Inputs.DeleteVideoId -> deleteVideo(input.videoId, videoRepository)
        is ProcessVideosContract.Inputs.HandleFiles -> handleFiles(input.files, videoRepository)
    }

    private suspend fun ProcessVideoInputScope.observeVideosIds() {
        sideJob("observeVideosIds") {
            videoRepository.flowOfVideosId().collect { videoIds ->
                postInput(ProcessVideosContract.Inputs.SetVideosIds(videos = videoIds))
            }
        }
    }

    private fun ProcessVideoInputScope.deleteVideo(videoId: String, videoRepository: VideoRepository) {
        cancelSideJob("observeVideo")
        cancelSideJob("startProcessingVideo")
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
