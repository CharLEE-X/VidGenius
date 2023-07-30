package com.charleex.vidgenius.youtube.youtube.video

// SOURCE: https://github.com/htchien/youtube-api-samples-kotlin/blob/master/src/main/kotlin/tw/htchien/youtube/api/data/UploadVideo.kt

import co.touchlab.kermit.Logger
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.googleapis.media.MediaHttpUploader
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.api.services.youtube.model.VideoStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.io.IOException

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
    ): Flow<UploadVideoProgress>
}

sealed interface UploadVideoProgress {
    data class Progress(val progress: Float) : UploadVideoProgress
    data class Success(val youtubeVideoId: String) : UploadVideoProgress
    data class Error(val message: String) : UploadVideoProgress
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
    ): Flow<UploadVideoProgress> = channelFlow {
        if (!videoFile.exists()) {
            send(UploadVideoProgress.Error("File does not exist"))
            return@channelFlow
        }

        logger.d { "Uploading: ${videoFile.path}" }
        try {
            send(UploadVideoProgress.Progress(.1f))
            val progressChannel = Channel<Float>()


//            val scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload")
//            val credential = googleAuth.authorize(scopes, "uploadvideo")
//
//            youtube = YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName(
//                "youtube-cmdline-uploadvideo-sample").build()


            val videoStatus = VideoStatus().apply {
                privacyStatus = "public"
            }

            val snippet = VideoSnippet().apply {
                setTitle(title)
                setDescription(description)
                setTags(tags)
            }

            val videoObjectDefiningMetadata = Video().apply {
                status = videoStatus
                setSnippet(snippet)
            }

            val mediaContent = InputStreamContent(VIDEO_FILE_FORMAT, videoFile.inputStream())

            val videoInsert = youtube.videos()
                .insert(listOf("snippet", "statistics", "status"), videoObjectDefiningMetadata, mediaContent)

            // Set the upload type and add an event listener.
            val uploader = videoInsert.mediaHttpUploader

            // Indicate whether direct media upload is enabled. A value of
            // "True" indicates that direct media upload is enabled and that
            // the entire media content will be uploaded in a single request.
            // A value of "False," which is the default, indicates that the
            // request will use the resumable media upload protocol, which
            // supports the ability to resume an upload operation after a
            // network interruption or other transmission failure, saving
            // time and bandwidth in the event of network failures.
            uploader.isDirectUploadEnabled = false


            send(UploadVideoProgress.Progress(.1f))

            val progressListener = MediaHttpUploaderProgressListener { mediaHttpUploader ->
                when (mediaHttpUploader.uploadState) {
                    MediaHttpUploader.UploadState.INITIATION_STARTED -> println("Initiation Started")
                    MediaHttpUploader.UploadState.INITIATION_COMPLETE -> println("Initiation Completed")
                    MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                        println("Upload in progress")
                        println("Upload percentage: " + mediaHttpUploader.progress)
                        println("Upload progress float: " + mediaHttpUploader.progress.toFloat())

                        val progress = mediaHttpUploader.progress.toFloat()
//                        launch {
//                            progressChannel.send(progress) // Send progress via the channel
//                        }
                    }

                    MediaHttpUploader.UploadState.MEDIA_COMPLETE -> println("Upload Completed!")
                    MediaHttpUploader.UploadState.NOT_STARTED -> println("Upload Not Started!")
                    null -> println("Upload state is null!")
                }
            }
            uploader.progressListener = progressListener

            val returnedVideo = videoInsert.execute()

            send(UploadVideoProgress.Progress(.9f))

            println("\n================== Returned Video ==================\n")
            println("  - Id: " + returnedVideo.id)
            println("  - Link: " + "https://www.youtube.com/watch?v=${returnedVideo.id}")
            println("  - Title: " + returnedVideo.snippet.title)
            println("  - Description: " + returnedVideo.snippet.description)
            println("  - Tags: " + returnedVideo.snippet.tags)
            println("  - Privacy Status: " + returnedVideo.status.privacyStatus)
            println("  - Published at: " + returnedVideo.snippet.publishedAt)

            send(UploadVideoProgress.Progress(1f))
            send(UploadVideoProgress.Success(returnedVideo.id))
            progressChannel.close()
        } catch (e: GoogleJsonResponseException) {
            System.err.println(
                "GoogleJsonResponseException code: " + e.details.code + " : "
                        + e.details.message
            )
            e.printStackTrace()
        } catch (e: IOException) {
            System.err.println("IOException: " + e.message)
            e.printStackTrace()
        } catch (t: Throwable) {
            System.err.println("Throwable: " + t.message)
            t.printStackTrace()
        }
    }
}
