package com.charleex.vidgenius.yt.youtube.video

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
import java.io.IOException
import java.util.Calendar

/**
 * Upload a video to the authenticated user's channel. Use OAuth 2.0 to
 * authorize the request. Note that you must add your video files to the
 * project folder to upload them with this application.
 */
interface UploadVideo {
    fun upload()
}

class UploadVideoImpl(
    private val logger: Logger,
    private val youtube: YouTube,
) : UploadVideo {
    private val VIDEO_FILE_FORMAT = "video/*"
    private val SAMPLE_VIDEO_FILENAME = "sample-video.mp4"

    /**
     * Upload the user-selected video to the user's YouTube channel. The code
     * looks for the video in the application's project folder and uses OAuth
     * 2.0 to authorize the API request.
     */
    override fun upload() {
        logger.d { "Uploading: $SAMPLE_VIDEO_FILENAME" }
        try {
            val videoStatus = VideoStatus().apply {
                privacyStatus = "public"
            }

            // Add extra information to the video before uploading.
            val videoObjectDefiningMetadata = Video().apply {
                status = videoStatus
            }

            // Most of the video's metadata is set on the VideoSnippet object.
            val snippet = VideoSnippet().apply {
                // This code uses a Calendar instance to create a unique name and
                // description for test purposes so that you can easily upload
                // multiple files. You should remove this code from your project
                // and use your own standard names instead.
                val cal = Calendar.getInstance()
                title = "Test Upload via Java on " + cal.time
                description = "Video uploaded via YouTube Data API V3 using the Java library " + "on " + cal.time
            }


            // Set the keyword tags that you want to associate with the video.
            val tags = ArrayList<String>()
            tags.add("test")
            tags.add("example")
            tags.add("java")
            tags.add("YouTube Data API V3")
            tags.add("erase me")
            snippet.tags = tags

            // Add the completed snippet object to the video resource.
            videoObjectDefiningMetadata.snippet = snippet

            val mediaContent = InputStreamContent(
                VIDEO_FILE_FORMAT,
                UploadVideo::class.java.getResourceAsStream("/sample-video.mp4")
            )

            // Insert the video. The command sends three arguments. The first
            // specifies which information the API request is setting and which
            // information the API response should return. The second argument
            // is the video resource that contains metadata about the new video.
            // The third argument is the actual video content.
            val videoInsert = youtube!!.videos()
                .insert("snippet,statistics,status", videoObjectDefiningMetadata, mediaContent)

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

            val progressListener = MediaHttpUploaderProgressListener { uploader ->
                when (uploader.uploadState) {
                    MediaHttpUploader.UploadState.INITIATION_STARTED -> println("Initiation Started")
                    MediaHttpUploader.UploadState.INITIATION_COMPLETE -> println("Initiation Completed")
                    MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> {
                        println("Upload in progress")
                        println("Upload percentage: " + uploader.progress)
                    }

                    MediaHttpUploader.UploadState.MEDIA_COMPLETE -> println("Upload Completed!")
                    MediaHttpUploader.UploadState.NOT_STARTED -> println("Upload Not Started!")
                }
            }
            uploader.progressListener = progressListener

            // Call the API and upload the video.
            val returnedVideo = videoInsert.execute()

            // Print data about the newly inserted video from the API response.
            println("\n================== Returned Video ==================\n")
            println("  - Id: " + returnedVideo.id)
            println("  - Title: " + returnedVideo.snippet.title)
            println("  - Tags: " + returnedVideo.snippet.tags)
            println("  - Privacy Status: " + returnedVideo.status.privacyStatus)
            println("  - Video Count: " + returnedVideo.statistics.viewCount)

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
