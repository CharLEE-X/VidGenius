package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.feature.process_videos.model.UIProgressState
import com.charleex.vidgenius.feature.process_videos.model.toUiVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
import kotlinx.coroutines.flow.onEach
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

    override suspend fun ProcessVideosInputScope.handleInput(
        input: ProcessVideosContract.Inputs,
    ) = when (input) {
        ProcessVideosContract.Inputs.ObserveVideos -> observeVideos()
        is ProcessVideosContract.Inputs.SetVideos -> updateState { it.copy(videos = input.videos) }
        is ProcessVideosContract.Inputs.DeleteVideoId -> deleteVideo(input.videoId, videoRepository)
        is ProcessVideosContract.Inputs.HandleFiles -> handleFiles(input.files, videoRepository)

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

    private fun ProcessVideosInputScope.deleteVideo(videoId: String, videoRepository: VideoRepository) {
        sideJob("deleteVideo") {
            videoRepository.deleteVideo(videoId)
        }
    }

    private suspend fun ProcessVideosInputScope.handleFiles(files: List<*>, videoRepository: VideoRepository) {
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
