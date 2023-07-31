package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.feature.process_videos.model.UIProgressState
import com.charleex.vidgenius.feature.process_videos.model.toUiVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.core.PrintlnLogger
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

        is ProcessVideosContract.Inputs.OnChildProgressStateChanged -> onChildProgressStateChanged(
            input.videoId,
            input.processingState
        )

        is ProcessVideosContract.Inputs.SetQueue -> updateState { it.copy(queue = input.queue) }
        is ProcessVideosContract.Inputs.SetProgress -> updateState { it.copy(progress = input.progress) }
        is ProcessVideosContract.Inputs.SetDone -> updateState { it.copy(done = input.done) }
        is ProcessVideosContract.Inputs.SetCanceled -> updateState { it.copy(canceled = input.canceled) }
        is ProcessVideosContract.Inputs.SetFailed -> updateState { it.copy(failed = input.failed) }
    }

    private suspend fun ProcessVideosInputScope.onChildProgressStateChanged(
        videoId: String,
        processingState: UIProgressState,
    ) {
        val state = getCurrentState()
        sideJob("onChildProgressStateChanged") {
            when (processingState) {
                UIProgressState.Success -> {
                    val done = state.done + videoId
                    postInput(ProcessVideosContract.Inputs.SetDone(done))
                    PrintlnLogger().info("Done: $videoId")

                    val queue = state.queue - videoId
                    postInput(ProcessVideosContract.Inputs.SetQueue(queue))
                    PrintlnLogger().info("Removed from Queue: $queue")
                }

                is UIProgressState.InProgress -> {
                    val progress = state.progress + videoId
                    postInput(ProcessVideosContract.Inputs.SetProgress(progress))
                    PrintlnLogger().info("Progress: $videoId ${processingState.progress}")
                }
                UIProgressState.Queued -> {
                    val queue = state.queue + videoId
                    postInput(ProcessVideosContract.Inputs.SetQueue(queue))
                    PrintlnLogger().info("Queued: $videoId")
                }
                UIProgressState.Cancelled -> {
                    val canceled = state.canceled + videoId
                    postInput(ProcessVideosContract.Inputs.SetCanceled(canceled))
                    PrintlnLogger().info("Cancelled: $videoId")

                    val queue = state.queue - videoId
                    postInput(ProcessVideosContract.Inputs.SetQueue(queue))
                    PrintlnLogger().info("Removed from Queue: $queue")

                    val progress = state.progress - videoId
                    postInput(ProcessVideosContract.Inputs.SetProgress(progress))
                    PrintlnLogger().info("Removed from Progress: $progress")
                }
                is UIProgressState.Error -> {
                    val failed = state.failed + videoId
                    postInput(ProcessVideosContract.Inputs.SetFailed(failed))
                    PrintlnLogger().info("Failed: $videoId")

                    val queue = state.queue - videoId
                    postInput(ProcessVideosContract.Inputs.SetQueue(queue))
                    PrintlnLogger().info("Removed from Queue: $queue")

                    val progress = state.progress - videoId
                    postInput(ProcessVideosContract.Inputs.SetProgress(progress))
                    PrintlnLogger().info("Removed from Progress: $progress")
                }
            }
        }
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
