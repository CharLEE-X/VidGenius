package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import com.charleex.vidgenius.datasource.repository.OpenAiRepository
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.flow.Flow
import java.io.File

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
    fun getVideos(): Flow<List<Video>>
    suspend fun filterVideosFromFiles(files: List<*>)
    suspend fun processAndUploadVideo(videoId: String, config: ProcessingConfig)
}

internal class VideoProcessingImpl(
    private val logger: Logger,
    private val videoRepository: VideoRepository,
    private val database: VidGeniusDatabase,
    private val googleCloudRepository: GoogleCloudRepository,
    private val openAiRepository: OpenAiRepository,
    private val youtubeRepository: YoutubeRepository,
) : VideoProcessing {
    override fun getVideos(): Flow<List<Video>> {
        return videoRepository.flowOfVideos()
    }

    override suspend fun filterVideosFromFiles(files: List<*>) {
        logger.d("Getting videos from files $files")
        videoRepository.filterVideos(files)
    }


    override suspend fun processAndUploadVideo(videoId: String, config: ProcessingConfig) {
        logger.d("Processing video $videoId")

        val video = videoRepository.getVideoById(videoId)

        val videoWithScreenshots = processVideo(video, config)

        val videoWithDescriptions = processScreenshotsToText(videoWithScreenshots, config.numberOfScreenshots)

        val videoWithDescriptionContext = processDescriptions(videoWithDescriptions)

        val videoWithMetadata = generateMetaData(videoWithDescriptionContext)

        if (config.uploadYouTube) {
            uploadYouTubeVideo(videoWithMetadata, config.channelId)
        }
    }

    private suspend fun processVideo(video: Video, config: ProcessingConfig): Video {
        val hasScreenshots = video.screenshots.size == config.numberOfScreenshots
        if (!hasScreenshots) {
            logger.d("No screenshots | ${video.id}")
        }
        val screenshotsHaveText = video.descriptions.all { it.isNotEmpty() }

        val allFilesExist = video.screenshots.all { File(it).exists() }
        if (hasScreenshots && screenshotsHaveText && allFilesExist) return video

        logger.d("Video processing | ${video.id} | Start")
        val screenshots = videoRepository.captureScreenshots(video, config.numberOfScreenshots)
        val newVideo = video.copy(screenshots = screenshots)
        database.videoQueries.upsert(newVideo)
        logger.d("Video processing | ${video.id} | Done | $screenshots")
        return newVideo
    }

    private suspend fun processScreenshotsToText(video: Video, numberOfScreenshots: Int): Video {
        val hasDescriptions = video.descriptions.size == numberOfScreenshots
        if (!hasDescriptions) {
            logger.d("No descriptions | ${video.id}")
        }
        val allDescriptionsHaveText = video.descriptions.all { it.isNotEmpty() }
        if (!allDescriptionsHaveText) {
            logger.d("Not all descriptions have text | ${video.id}")
        }
        if (hasDescriptions && allDescriptionsHaveText) return video

        logger.d("Text processing | ${video.id} | Start")
        val descriptions = video.screenshots.map { screenshot ->
            googleCloudRepository.getTextFromImage(screenshot)
        }
        val newVideo = video.copy(descriptions = descriptions)
        database.videoQueries.upsert(newVideo)
        logger.d("Text processing | ${video.id} | Done | $descriptions")
        return newVideo
    }

    private suspend fun processDescriptions(video: Video): Video {
        val hasDescriptionContext = video.descriptionContext.isNullOrEmpty().not()
        if (hasDescriptionContext) return video

        logger.d("Description processing | ${video.id} | Start")
        val context = openAiRepository.getDescriptionContext(video.descriptions)
        val newVideo = video.copy(descriptionContext = context)
        database.videoQueries.upsert(newVideo)
        logger.d("Description processing | ${video.id} | Done | $context")
        return newVideo
    }

    private suspend fun generateMetaData(video: Video): Video {
        val hasTitle = video.title?.isNotEmpty() == true
        val hasDescription = video.description?.isNotEmpty() == true
        val hasTags = video.tags.isNotEmpty()
        val tagsHaveText = video.tags.all { it.isNotEmpty() }
        if (hasTitle && hasDescription && hasTags && tagsHaveText) return video

        logger.d("Metadata generation | ${video.id} | Start")
        val metadata = openAiRepository.getMetaData(video)
        val newVideo = video.copy(
            title = metadata.title,
            description = metadata.description,
            tags = metadata.tags,
        )
        database.videoQueries.upsert(newVideo)
        logger.d("Metadata generation | ${video.id} | Done | $metadata")
        return newVideo
    }

    private suspend fun uploadYouTubeVideo(
        video: Video,
        channelId: String,
    ): Video {
        logger.d("Upload YouTube video | ${video.id} | Start")
        val youtubeVideoId = youtubeRepository.uploadVideo(video, channelId)
        val newVideo = video.copy(youtubeVideoId = youtubeVideoId)
        database.videoQueries.upsert(newVideo)
        logger.d("Upload YouTube video | ${video.id} | Done | $youtubeVideoId")
        return newVideo
    }
}

