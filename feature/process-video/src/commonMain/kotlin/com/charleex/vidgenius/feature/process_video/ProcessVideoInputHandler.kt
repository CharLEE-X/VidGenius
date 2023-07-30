package com.charleex.vidgenius.feature.process_video

import com.charleex.vidgenius.datasource.GoogleCloudRepository
import com.charleex.vidgenius.datasource.OpenAiRepository
import com.charleex.vidgenius.datasource.VideoRepository
import com.charleex.vidgenius.feature.process_video.state.handleDescriptions
import com.charleex.vidgenius.feature.process_video.state.handleDragDrop
import com.charleex.vidgenius.feature.process_video.state.handleMetaData
import com.charleex.vidgenius.feature.process_video.state.handleScreenshots
import com.charleex.vidgenius.feature.process_video.state.handleUploads
import com.charleex.vidgenius.feature.process_video.state.handleVideo
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal typealias ProcessVideoInputScope = InputHandlerScope<
        ProcessVideoContract.Inputs,
        ProcessVideoContract.Events,
        ProcessVideoContract.State>

internal class ProcessVideoInputHandler :
    KoinComponent,
    InputHandler<ProcessVideoContract.Inputs, ProcessVideoContract.Events, ProcessVideoContract.State> {

    private val videoRepository: VideoRepository by inject()
    private val googleCloudRepository: GoogleCloudRepository by inject()
    private val openAiRepository: OpenAiRepository by inject()
//    private val youtubeRepository: YoutubeRepository by inject()

    override suspend fun ProcessVideoInputScope.handleInput(
        input: ProcessVideoContract.Inputs,
    ) = when (input) {
        is ProcessVideoContract.Inputs.Video -> handleVideo(input, videoRepository)
        is ProcessVideoContract.Inputs.DragDrop -> handleDragDrop(input, videoRepository)
        is ProcessVideoContract.Inputs.Screenshots -> handleScreenshots(input, videoRepository, openAiRepository)
        is ProcessVideoContract.Inputs.Description -> handleDescriptions(input, googleCloudRepository, openAiRepository)
        is ProcessVideoContract.Inputs.MetaData -> handleMetaData(input, openAiRepository, videoRepository)
        is ProcessVideoContract.Inputs.Upload -> handleUploads(input,
//            youtubeRepository
        )
    }
}
