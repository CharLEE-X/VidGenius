package com.charleex.vidgenius.youtube.youtube.video

// SOURCE: https://github.com/htchien/youtube-api-samples-kotlin/blob/master/src/main/kotlin/tw/htchien/youtube/api/data/UploadVideo.kt

import co.touchlab.kermit.Logger
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import java.io.File

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 */
interface UploadVideoService {
    fun uploadVideo(
        videoFile: File,
        title: String,
        description: String,
        tags: List<String>,
        channelId: String,
    ): String
}


class UploadVideoServiceImpl(
    private val logger: Logger,
    private val youtube: YouTube,
) : UploadVideoService {
    companion object {
        private const val VIDEO_FILE_FORMAT = "video/*"
    }

    override fun uploadVideo(
        videoFile: File,
        title: String,
        description: String,
        tags: List<String>,
        channelId: String,
    ): String {
        logger.d { "Uploading: ${videoFile.path}" }
        val videoStatus = VideoStatus().apply {
            privacyStatus = "public"
        }

        val snippet = VideoSnippet().apply {
            setTitle(title)
            setDescription(description)
            setTags(tags)
            setChannelId(channelId)
        }

        val videoObjectDefiningMetadata = Video().apply {
            status = videoStatus
            setSnippet(snippet)
        }

        val mediaContent = InputStreamContent(VIDEO_FILE_FORMAT, videoFile.inputStream())

        val videoInsert = youtube.videos()
            .insert(listOf("snippet", "statistics", "status"), videoObjectDefiningMetadata, mediaContent)
//                .setOnBehalfOfContentOwner("joFpbRmICEmDzE276LP59g")
//                .setOnBehalfOfContentOwnerChannel(channelId)

        val uploader = videoInsert.mediaHttpUploader
        uploader.isDirectUploadEnabled = false

        val progressListener = MediaHttpUploaderProgressListener { mediaHttpUploader ->
            println("Upload state: ${mediaHttpUploader.uploadState}")
        }
        uploader.progressListener = progressListener

        val returnedVideo = videoInsert.execute() ?: error("No video uploaded.")

        println("\n================== Returned Video ==================\n")
        println("  - Id: " + returnedVideo.id)
        println("  - Link: " + "https://www.youtube.com/watch?v=${returnedVideo.id}")
        println("  - Title: " + returnedVideo.snippet.title)
        println("  - Description: " + returnedVideo.snippet.description)
        println("  - Tags: " + returnedVideo.snippet.tags)
        println("  - Privacy Status: " + returnedVideo.status.privacyStatus)
        println("  - Published at: " + returnedVideo.snippet.publishedAt)

        return returnedVideo.id
    }
}
