package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.local_video.LocalVideoProcessor
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import com.charleex.vidgenius.datasource.feature.youtube.model.Category
import com.charleex.vidgenius.datasource.model.LocalVideo
import com.charleex.vidgenius.datasource.model.YtVideo
import com.charleex.vidgenius.datasource.utils.DateTimeService
import com.charleex.vidgenius.datasource.utils.UuidProvider
import com.charleex.vidgenius.open_ai.OpenAiRepository
import com.charleex.vidgenius.vision_ai.GoogleCloudRepository
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

interface VideoProcessing {
    suspend fun addLocalVideos(files: List<*>)
    fun flowOfVideos(): Flow<List<Video>>
    fun getVideoByIdFlow(id: String): Flow<Video>
    fun getVideoById(id: String): Video
    fun deleteLocalVideo(video: Video)

    suspend fun processVideoToScreenshots(
        video: Video,
        numberOfScreenshots: Int,
        onError: (String) -> Unit,
    )

    fun signOut()
}

internal class VideoProcessingImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val localVideoProcessor: LocalVideoProcessor,
    private val googleCloudRepository: GoogleCloudRepository,
    private val openAiRepository: OpenAiRepository,
    private val youtubeRepository: YoutubeRepository,
    private val uuidProvider: UuidProvider,
    private val datetimeService: DateTimeService,
) : VideoProcessing {
    companion object {
        val languageCodes = listOf("en-US", "es", "zh", "pt", "hi")
    }

    override fun flowOfVideos(): Flow<List<Video>> =
        database.videoQueries.getAll().asFlow().map { it.executeAsList() }

    override fun getVideoByIdFlow(id: String): Flow<Video> {
        return database.videoQueries.getById(id).asFlow().mapToOne()
    }

    override fun getVideoById(id: String): Video {
        return database.videoQueries.getById(id).executeAsOne()
    }

    override suspend fun addLocalVideos(files: List<*>) {
        logger.d("Adding videos $files")
        val filteredFiles = localVideoProcessor.filterVideos(files)
        val localVideos = filteredFiles.map { videoFile ->
            val videoName = videoFile.nameWithoutExtension
                .replace("_", " ")
            logger.d("Storing video ${videoFile.absolutePath} | youtubeVideoId: $videoName")
            LocalVideo(
                id = uuidProvider.uuid(),
                name = videoName,
                path = videoFile.absolutePath,
                screenshots = emptyList(),
                descriptions = emptyList(),
                descriptionContext = null,
                localizations = emptyMap(),
                isCompleted = false,
                createdAt = datetimeService.nowInstant(),
                modifiedAt = datetimeService.nowInstant(),
            )
        }

        // TODO: Handle LocalVideos
    }

    override fun deleteLocalVideo(video: Video) {
        logger.d("Deleting video ${video.id}")
        val newVideo = video.copy(localVideo = null)
        database.videoQueries.upsert(newVideo)
    }

    override suspend fun processVideoToScreenshots(
        video: Video,
        numberOfScreenshots: Int,
        onError: (String) -> Unit,
    ) {
        if (video.localVideo == null) return
        if (video.ytVideo == null) return

        logger.d("Processing video ${video.id}")
        val config = database.configQueries.getAll().executeAsOne()

        val videoWithScreenshots = processVideoToScreenshots(video.localVideo, 3)
        val videoWithDescriptions =
            processScreenshotsToText(videoWithScreenshots, numberOfScreenshots)
        val videoWithDescriptionContext =
            processDescriptionsToContext(videoWithDescriptions, config.category)

        logger.d { "Context: $videoWithDescriptionContext" }

        val videoWithMetadata = generateMetaData(videoWithDescriptionContext, config.category)
        updateYouTubeVideo(video.ytVideo, videoWithMetadata)
        logger.v { "Processing ${video.id} | Finished" }
    }

    override fun signOut() {
        youtubeRepository.signOut()
    }

    //    private fun moveFileToUploaded(finalVideo: Video) {
//        val file = File(finalVideo.path)
//        val parentDir = file.parent
//        val fileName = file.name
//        val extension = file.extension
//        val newDir = "yt-uploaded"
//        val newPath = "$parentDir/${newDir}/${fileName}.${extension}"
//        val movedPath = Files.move(file.toPath(), newPath.toPath().toFile().toPath())
//        val newVideo = finalVideo.copy(path = movedPath.toAbsolutePath().toString())
//        database.videoQueries.upsert(newVideo)
//        logger.d("Video renamed | ${finalVideo.id} | $fileName -> $newPath")
//    }
//
//    private fun renameVideo(video: Video): Video {
//        val title = video.title
//        val file = File(video.path)
//        val oldDirectory = file.parent
//        val oldName = file.name
//        val oldExtension = file.extension
//        val newName = "${title}.${oldExtension}"
//        if (video.path.contains(newName)) return video
//        val newFileName = renameFile(oldDirectory, oldName, newName)
//        val newPath = "$oldDirectory/${newFileName}.${oldExtension}"
//        val newVideo = video.copy(path = newPath)
//        database.videoQueries.upsert(newVideo)
//        logger.d("Video renamed | ${video.id} | $oldName -> $newPath")
//        return newVideo
//    }
//
//    private fun String.removeEmojis(): String {
//        val emojiRegex = Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]+")
//        return this.replace(emojiRegex, "")
//    }
//
    private suspend fun processVideoToScreenshots(
        localVideo: LocalVideo,
        numberOfScreenshots: Int,
    ): LocalVideo {
        val hasScreenshots = localVideo.screenshots.size == numberOfScreenshots
        if (!hasScreenshots) {
            logger.d("No screenshots | ${localVideo.id}")
        }
        val screenshotsHaveText = localVideo.descriptions.all { it.isNotEmpty() }

        val allFilesExist = localVideo.screenshots.all { File(it).exists() }
        if (hasScreenshots && screenshotsHaveText && allFilesExist) {
            logger.d("Screenshots already exist | ${localVideo.id}")
            return localVideo
        }

        logger.d("Video processing | ${localVideo.id} | Start")
        val screenshots = try {
            localVideoProcessor.captureScreenshots(localVideo.path, numberOfScreenshots)
        } catch (e: Exception) {
            logger.e(e) { "Error generating screenshots" }
            throw e
        }
        logger.d("Video processing | ${localVideo.id} | Done | $screenshots")
        return localVideo.copy(screenshots = screenshots)
    }

    private suspend fun processScreenshotsToText(
        localVideo: LocalVideo,
        numberOfScreenshots: Int,
    ): LocalVideo {
        val hasDescriptions = localVideo.descriptions.size == numberOfScreenshots
        if (!hasDescriptions) {
            logger.d("No descriptions | ${localVideo.id}")
        }
        val allDescriptionsHaveText = localVideo.descriptions.all { it.isNotEmpty() }
        if (!allDescriptionsHaveText) {
            logger.d("Not all descriptions have text | ${localVideo.id}")
        }
        if (hasDescriptions && allDescriptionsHaveText) {
            logger.d("Already has descriptions | ${localVideo.id}")
            return localVideo
        }

        logger.d("Text processing | ${localVideo.id} | Start")
        val descriptions = try {
            googleCloudRepository.getDescriptionsFromScreenshots(localVideo.screenshots)
        } catch (e: Exception) {
            logger.e(e) { "Error generating descriptions" }
            throw e
        }
        val newVideo = localVideo.copy(descriptions = descriptions)
        logger.d("Text processing | ${localVideo.id} | Done | ${newVideo.descriptions}")
        return newVideo
    }

    private suspend fun processDescriptionsToContext(
        localVideo: LocalVideo,
        category: Category,
    ): LocalVideo {
        val hasDescriptionContext = localVideo.descriptionContext.isNullOrEmpty().not()
        if (hasDescriptionContext) {
            logger.d("Already has description context | ${localVideo.id}")
            return localVideo
        }

        logger.d("Description processing | ${localVideo.id} | Start")
        val context = try {
            openAiRepository.getContextFromDescriptions(localVideo.descriptions, category.query)
        } catch (e: Exception) {
            logger.e(e) { "Error generating description context" }
            throw e
        }
        logger.d("Description processing | ${localVideo.id} | Done | $context")
        return localVideo.copy(descriptionContext = context)
    }

    private suspend fun generateMetaData(localVideo: LocalVideo, category: Category): LocalVideo {
//        val hasContentInfoEnUS = localVideo.contentInfo?.enUS?.title?.isNotEmpty() == true &&
//                localVideo.contentInfo.enUS.description.isNotEmpty()
//        val hasContentInfoPt = localVideo.contentInfo?.pt?.title?.isNotEmpty() == true &&
//                localVideo.contentInfo.pt.description.isNotEmpty()
//        val hasContentInfoEs = localVideo.contentInfo?.es?.title?.isNotEmpty() == true &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        val hasContentInfoFr = localVideo.contentInfo?.zh?.title?.isNotEmpty() == true &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        val hasContentInfoHi = localVideo.contentInfo?.hi?.title?.isNotEmpty() == true &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        val hasTags = localVideo.contentInfo?.tags?.isNotEmpty() == true
//        val tagsHaveText = localVideo.contentInfo?.tags?.all { it.isNotEmpty() } == true
//
//        if (
//            hasContentInfoEnUS &&
//            hasContentInfoPt &&
//            hasContentInfoEs &&
//            hasContentInfoFr &&
//            hasContentInfoHi &&
//            hasTags &&
//            tagsHaveText
//        ) {
//            logger.d("Metadata generation | ${localVideo.id} | Already has metadata")
        return localVideo
//        }
//
//        logger.d("Metadata generation | ${localVideo.id} | Start")
//        val contentInfo = try {
//            openAiRepository.getContentInfo(localVideo.descriptions, category.query, languageCodes)
//        } catch (e: Exception) {
//            logger.e(e) { "Error generating metadata" }
//            throw e
//        }
//        logger.d("Metadata generation | ${localVideo.id} | Done")
//        return localVideo.copy(contentInfo = contentInfo)
    }

    private suspend fun updateYouTubeVideo(
        ytVideo: YtVideo,
        localVideo: LocalVideo,
    ) {
        logger.d("Updating YouTube video ${ytVideo.id} | Start")
//        val result = youtubeRepository.updateVideo(ytVideo, localVideo)
//        if (result) {
//            val newVideo = localVideo.copy(isCompleted = true)
//            logger.d("Upload YouTube video | ${localVideo.id} | Done | $result")
//        } else {
//            logger.e { "Error updating YouTube video | ${ytVideo.id}" }
//        }
    }
}
