package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.model.ProgressState
import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import com.charleex.vidgenius.datasource.repository.OpenAiRepository
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

sealed interface ProcessingState {
    data class VideoProcessing(
        val progressState: ProgressState = ProgressState.None,
    ) : ProcessingState

    data class TextProcessing(
        val progressState: ProgressState = ProgressState.None,
    ) : ProcessingState

    data class MetadataGeneration(
        val progressState: ProgressState = ProgressState.None,
    ) : ProcessingState

    sealed interface UploadVideo : ProcessingState {
        data class Youtube(
            val progressState: ProgressState = ProgressState.None,
        ) : UploadVideo
    }

    object Done : ProcessingState
}

data class ProcessingConfig(
    val id: String = uuid4().toString(),
    val channelId: String,
    val numberOfScreenshots: Int,
    val category: VideoCategory,
    val uploadYouTube: Boolean,
)

data class VideoCategory(
    val id: String = uuid4().toString(),
    val name: String,
)

interface VideoProcessing {
    fun getVideoIds(): Flow<List<String>>
    suspend fun filterVideosFromFiles(files: List<*>)
    fun processVideo(videoId: String, config: ProcessingConfig): Flow<ProcessingState>
}

internal class VideoProcessingImpl(
    private val logger: Logger,
    private val videoRepository: VideoRepository,
    private val database: VidGeniusDatabase,
    private val googleCloudRepository: GoogleCloudRepository,
    private val openAiRepository: OpenAiRepository,
    private val youtubeRepository: YoutubeRepository,
) : VideoProcessing {
    override fun getVideoIds(): Flow<List<String>> {
        return videoRepository.flowOfVideosId()
    }

    override suspend fun filterVideosFromFiles(files: List<*>) {
        logger.d("Getting videos from files $files")
        videoRepository.filterVideos(files)
    }


    override fun processVideo(videoId: String, config: ProcessingConfig): Flow<ProcessingState> = flow {
        logger.d("Processing video $videoId")
        setupProcessing(config)
        processVideo(videoId, config) { message ->
            emit(ProcessingState.VideoProcessing(ProgressState.Error(message)))
            emit(ProcessingState.TextProcessing(ProgressState.Cancelled))
            emit(ProcessingState.MetadataGeneration(ProgressState.Cancelled))
            cancelAllUploads(config)
        }
        processText(videoId, config) { message ->
            emit(ProcessingState.TextProcessing(ProgressState.Error(message)))
            emit(ProcessingState.MetadataGeneration(ProgressState.Cancelled))
            cancelAllUploads(config)
        }
        generateMetadata(videoId) { message ->
            emit(ProcessingState.MetadataGeneration(ProgressState.Error(message)))
            cancelAllUploads(config)
            currentCoroutineContext().cancel()
        }
        if (config.uploadYouTube) {
            uploadToYoutube(videoId, config) { message ->
                emit(ProcessingState.UploadVideo.Youtube(ProgressState.Error(message)))
            }
        }
    }

    private suspend fun FlowCollector<ProcessingState>.setupProcessing(config: ProcessingConfig) {
        logger.d("Processing setup")
        emit(ProcessingState.VideoProcessing(ProgressState.Queued))
        emit(ProcessingState.TextProcessing(ProgressState.Queued))
        emit(ProcessingState.MetadataGeneration(ProgressState.Queued))
        if (config.uploadYouTube) {
            emit(ProcessingState.UploadVideo.Youtube(ProgressState.Queued))
        }
    }

    private suspend fun FlowCollector<ProcessingState>.cancelAllUploads(
        config: ProcessingConfig,
    ) {
        if (config.uploadYouTube) {
            emit(ProcessingState.UploadVideo.Youtube(ProgressState.Cancelled))
        }
        currentCoroutineContext().cancel()
    }

    private suspend fun FlowCollector<ProcessingState>.processVideo(
        videoId: String,
        config: ProcessingConfig,
        onError: suspend (String) -> Unit,
    ) {
        logger.d("Video processing | $videoId")
        try {
            emit(ProcessingState.VideoProcessing(ProgressState.InProgress(0f)))

            videoRepository.captureScreenshots(videoId, config.numberOfScreenshots) { progress ->
                emit(ProcessingState.VideoProcessing(ProgressState.InProgress(progress)))
            }

            emit(ProcessingState.VideoProcessing(ProgressState.Success))
        } catch (e: Exception) {
            val message = e.message ?: "Video processing failed"
            onError(message)
        }
    }

    private suspend fun FlowCollector<ProcessingState>.processText(
        videoId: String,
        config: ProcessingConfig,
        onError: suspend (String) -> Unit,
    ) {
        logger.d("Text processing | $videoId")
        try {
            emit(ProcessingState.TextProcessing(ProgressState.InProgress(0f)))

            val totalSteps = config.numberOfScreenshots + 1
            logger.d("Text processing | Steps: $totalSteps")

            val video = videoRepository.flowOfVideo(videoId).first()

            val descriptions = video.screenshots.mapIndexed { index, screenshot ->
                val description = googleCloudRepository.getTextFromImage(screenshot.path)
                val progress = (index + 1) / totalSteps.toFloat()

                emit(ProcessingState.TextProcessing(ProgressState.InProgress(progress)))
                description
            }

            val descriptionContext = openAiRepository.getDescriptionContext(descriptions)

            emit(ProcessingState.TextProcessing(ProgressState.InProgress(1f)))

            val updatedVideo = video.copy(
                descriptions = descriptions,
                descriptionContext = descriptionContext,
            )

            database.videoQueries.upsert(updatedVideo)
            emit(ProcessingState.TextProcessing(ProgressState.Success))
        } catch (e: Exception) {
            val message = e.message ?: "Text processing failed"
            onError(message)
        }
    }

    private suspend fun FlowCollector<ProcessingState>.generateMetadata(
        videoId: String,
        onError: suspend (String) -> Unit,
    ) {
        logger.d("Metadata generation | $videoId")
        try {
            emit(ProcessingState.MetadataGeneration(ProgressState.InProgress(0f)))

            val video = videoRepository.flowOfVideo(videoId).first()
            val descriptionContext = video.descriptionContext ?: error("Missing description context.")

            openAiRepository.getMetaData(videoId, descriptionContext).collect { progress ->
                emit(ProcessingState.MetadataGeneration(ProgressState.InProgress(progress)))
            }
            emit(ProcessingState.TextProcessing(ProgressState.Success))
        } catch (e: Exception) {
            val message = e.message ?: "Text processing failed"
            onError(message)
        }
    }

    private suspend fun FlowCollector<ProcessingState>.uploadToYoutube(
        videoId: String,
        config: ProcessingConfig,
        onError: suspend (String) -> Unit,
    ) {
        logger.d("Upload YouTube video | $videoId")
        try {
            emit(ProcessingState.UploadVideo.Youtube(ProgressState.InProgress(0f)))

            val channelId = config.channelId
            youtubeRepository.uploadVideo(videoId, channelId).collect { progress ->
                emit(ProcessingState.UploadVideo.Youtube(ProgressState.InProgress(progress)))
            }

            emit(ProcessingState.UploadVideo.Youtube(ProgressState.Success))
        } catch (e: Exception) {
            val message = e.message ?: "Text processing failed"
            onError(message)
        }
    }
}
