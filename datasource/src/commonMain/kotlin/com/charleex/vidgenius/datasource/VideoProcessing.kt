package com.charleex.vidgenius.datasource

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.open_ai.OpenAiRepository
import com.charleex.vidgenius.datasource.feature.video_file.VideoFileRepository
import com.charleex.vidgenius.datasource.feature.vision_ai.GoogleCloudRepository
import com.charleex.vidgenius.datasource.feature.youtube.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.YoutubeRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.File

interface VideoProcessing {
    val videos: Flow<List<Video>>
    val ytVideos: Flow<List<YtVideo>>

    fun fetchUploads()
    fun addVideos(files: List<*>)
    fun deleteVideo(videoId: String)
    fun processAll(videos: List<Video>, onError: (String) -> Unit)

    fun signOut()
}

internal class VideoProcessingImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val videoFileRepository: VideoFileRepository,
    private val googleCloudRepository: GoogleCloudRepository,
    private val openAiRepository: OpenAiRepository,
    private val youtubeRepository: YoutubeRepository,
    private val scope: CoroutineScope,
) : VideoProcessing {
    override val videos: Flow<List<Video>>
        get() = videoFileRepository.flowOfVideos()

    override val ytVideos: Flow<List<YtVideo>>
        get() = youtubeRepository.flowOfYtVideos()

    override fun fetchUploads() {
        logger.d("Fetching uploads")
        scope.launch {
            youtubeRepository.fetchUploads()
        }
    }

    override fun addVideos(files: List<*>) {
        logger.d("Adding videos $files")
        scope.launch {
            videoFileRepository.filterVideos(files)
        }
    }

    override fun deleteVideo(videoId: String) {
        logger.d("Deleting video $videoId")
        videoFileRepository.deleteVideo(videoId)
    }

    override fun processAll(videos: List<Video>, onError: (String) -> Unit) {
        scope.launch {
            val jobs = videos.map { video ->
                // Start each processVideo coroutine asynchronously
                scope.async {
                    startProcessing(video, 3) {
                        onError(it)
                    }
                }
            }

            awaitAll(*jobs.toTypedArray())
        }
    }

    private suspend fun startProcessing(
        video: Video,
        numberOfScreenshots: Int,
        onError: (String) -> Unit,
    ) {
        logger.d("Processing video ${video.id}")
        try {
            val ytVideo = youtubeRepository.flowOfYtVideos().first()
                .firstOrNull { it.title == video.youtubeId }
                ?: error("No yt video found for ${video.youtubeId}")

            val videoWithScreenshots = processVideo(video, 3)
            val videoWithDescriptions =
                processScreenshotsToText(videoWithScreenshots, numberOfScreenshots)
            val videoWithDescriptionContext = processDescriptions(videoWithDescriptions)

            logger.d { "Context: $videoWithDescriptionContext" }

            val videoWithMetadata = generateMetaData(videoWithDescriptionContext)
            updateYouTubeVideo(ytVideo, videoWithMetadata)
            logger.v { "Processing $video | Finished" }
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.message ?: "Error while processing ${video.id}")
        }
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
    private suspend fun processVideo(video: Video, numberOfScreenshots: Int): Video {
        val hasScreenshots = video.screenshots.size == numberOfScreenshots
        if (!hasScreenshots) {
            logger.d("No screenshots | ${video.id}")
        }
        val screenshotsHaveText = video.descriptions.all { it.isNotEmpty() }

        val allFilesExist = video.screenshots.all { File(it).exists() }
        if (hasScreenshots && screenshotsHaveText && allFilesExist) return video

        logger.d("Video processing | ${video.id} | Start")
        val newVideo = try {
            videoFileRepository.captureScreenshots(video, numberOfScreenshots)
        } catch (e: Exception) {
            logger.e(e) { "Error generating screenshots" }
            throw e
        }
        logger.d("Video processing | ${video.id} | Done | ${newVideo.screenshots}")
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
        val newVideo = try {
            googleCloudRepository.getTextFromImages(video)
        } catch (e: Exception) {
            logger.e(e) { "Error generating descriptions" }
            throw e
        }
        logger.d("Text processing | ${video.id} | Done | ${newVideo.descriptions}")
        return newVideo
    }

    private suspend fun processDescriptions(video: Video): Video {
        val hasDescriptionContext = video.descriptionContext.isNullOrEmpty().not()
        if (hasDescriptionContext) return video

        logger.d("Description processing | ${video.id} | Start")
        val newVideo = try {
            openAiRepository.getDescriptionContext(video)
        } catch (e: Exception) {
            logger.e(e) { "Error generating description context" }
            throw e
        }
        logger.d("Description processing | ${video.id} | Done | ${newVideo.descriptionContext}")
        return newVideo
    }

    private suspend fun generateMetaData(video: Video): Video {
        val hasTitle = video.title?.isNotEmpty() == true
        val hasDescription = video.description?.isNotEmpty() == true
        val hasTags = video.tags.isNotEmpty()
        val tagsHaveText = video.tags.all { it.isNotEmpty() }
        if (hasTitle && hasDescription && hasTags && tagsHaveText) return video

        logger.d("Metadata generation | ${video.id} | Start")
        val newVideo = try {
            openAiRepository.getMetaData(video)
        } catch (e: Exception) {
            logger.e(e) { "Error generating metadata" }
            throw e
        }
        logger.d("Metadata generation | ${video.id} | Done | ${newVideo.title}")
        return newVideo
    }

    private suspend fun updateYouTubeVideo(
        ytVideo: YtVideo,
        video: Video,
    ) {
        logger.d("Updating YouTube video | ${video.youtubeId} | Start")
        val result = youtubeRepository.updateVideo(ytVideo, video)
        if (result) {
            val newVideo = video.copy(isCompleted = true)
            database.videoQueries.upsert(newVideo)
            database.ytVideoQueries.delete(video.youtubeId)
            logger.d("Upload YouTube video | ${video.id} | Done | $result")
        } else {
            logger.e { "Error updating YouTube video | ${video.youtubeId}" }
        }
    }
}
