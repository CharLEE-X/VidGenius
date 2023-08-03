package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.repository.GoogleCloudRepository
import com.charleex.vidgenius.datasource.repository.OpenAiRepository
import com.charleex.vidgenius.datasource.repository.VideoRepository
import com.charleex.vidgenius.datasource.repository.YoutubeRepository
import com.charleex.vidgenius.datasource.utils.renameFile
import com.hackathon.cda.repository.db.VidGeniusDatabase
import kotlinx.coroutines.flow.Flow
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Files

data class VideoCategory(
    val id: String = uuid4().toString(),
    val name: String,
)

interface VideoProcessing {
    fun getVideos(): Flow<List<Video>>
    suspend fun filterVideosFromFiles(files: List<*>)
    suspend fun processAndUploadVideo(
        videoId: String,
        channelId: String,
        numberOfScreenshots: Int,
        category: String,
        uploadYouTube: Boolean,
    )
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


    override suspend fun processAndUploadVideo(
        videoId: String,
        channelId: String,
        numberOfScreenshots: Int,
        category: String,
        uploadYouTube: Boolean,
    ) {
        logger.d("Processing video $videoId")

        val video = videoRepository.getVideoById(videoId)
        val videoWithScreenshots = processVideo(video, numberOfScreenshots)
        val videoWithDescriptions =
            processScreenshotsToText(videoWithScreenshots, numberOfScreenshots)
        val videoWithDescriptionContext = processDescriptions(videoWithDescriptions)
        val videoWithMetadata = generateMetaData(videoWithDescriptionContext)
        val finalVideo = if (uploadYouTube) videoWithMetadata else {
            renameVideo(videoWithMetadata)
        }
        if (uploadYouTube) {
            uploadYouTubeVideo(finalVideo, channelId)
            moveFileToUploaded(finalVideo)
        }
    }

    private fun moveFileToUploaded(finalVideo: Video) {
        val file = File(finalVideo.path)
        val parentDir = file.parent
        val fileName = file.name
        val extension = file.extension
        val newDir = "yt-uploaded"
        val newPath = "$parentDir/${newDir}/${fileName}.${extension}"
        val movedPath = Files.move(file.toPath(), newPath.toPath().toFile().toPath())
        val newVideo = finalVideo.copy(path = movedPath.toAbsolutePath().toString())
        database.videoQueries.upsert(newVideo)
        logger.d("Video renamed | ${finalVideo.id} | $fileName -> $newPath")
    }

    private fun renameVideo(video: Video): Video {
        val title = video.title
        val file = File(video.path)
        val oldDirectory = file.parent
        val oldName = file.name
        val oldExtension = file.extension
        val newName = "${title}.${oldExtension}"
        if (video.path.contains(newName)) return video
        val newFileName = renameFile(oldDirectory, oldName, newName)
        val newPath = "$oldDirectory/${newFileName}.${oldExtension}"
        val newVideo = video.copy(path = newPath)
        database.videoQueries.upsert(newVideo)
        logger.d("Video renamed | ${video.id} | $oldName -> $newPath")
        return newVideo
    }

    private fun String.removeEmojis(): String {
        val emojiRegex = Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")
        return this.replace(emojiRegex, "")
    }

    private suspend fun processVideo(video: Video, numberOfScreenshots: Int): Video {
        val hasScreenshots = video.screenshots.size == numberOfScreenshots
        if (!hasScreenshots) {
            logger.d("No screenshots | ${video.id}")
        }
        val screenshotsHaveText = video.descriptions.all { it.isNotEmpty() }

        val allFilesExist = video.screenshots.all { File(it).exists() }
        if (hasScreenshots && screenshotsHaveText && allFilesExist) return video

        logger.d("Video processing | ${video.id} | Start")
        val screenshots = try {
            videoRepository.captureScreenshots(video, numberOfScreenshots)
        } catch (e: Exception) {
            logger.e(e) { "Error generating screenshots" }
            throw e
        }
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
        val descriptions = try {
            video.screenshots.map { screenshot ->
                googleCloudRepository.getTextFromImage(screenshot)
            }
        } catch (e: Exception) {
            logger.e(e) { "Error generating descriptions" }
            throw e
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
        val context = try {
            openAiRepository.getDescriptionContext(video.descriptions)
        } catch (e: Exception) {
            logger.e(e) { "Error generating description context" }
            throw e
        }
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
        val metadata = try {
            openAiRepository.getMetaData(video)
        } catch (e: Exception) {
            logger.e(e) { "Error generating metadata" }
            throw e
        }
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
