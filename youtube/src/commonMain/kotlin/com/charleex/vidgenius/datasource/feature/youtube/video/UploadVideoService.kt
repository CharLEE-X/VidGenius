//package com.charleex.vidgenius.datasource.feature.youtube.video
//
//// SOURCE: https://github.com/htchien/youtube-api-samples-kotlin/blob/master/src/main/kotlin/tw/htchien/youtube/api/data/UploadVideo.kt
//
//import co.touchlab.kermit.Logger
//import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
//import com.google.api.client.auth.oauth2.TokenResponseException
//import com.google.api.client.googleapis.json.GoogleJsonResponseException
//import com.google.api.client.googleapis.media.MediaHttpUploader
//import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener
//import com.google.api.client.http.HttpTransport
//import com.google.api.client.http.InputStreamContent
//import com.google.api.client.json.JsonFactory
//import com.google.api.services.youtube.YouTube
//import com.google.api.services.youtube.model.Video
//import com.google.api.services.youtube.model.VideoSnippet
//import com.google.api.services.youtube.model.VideoStatus
//import com.google.common.collect.Lists
//import java.io.File
//
//interface UploadVideoService {
//    fun uploadVideo(
//        config: String,
//        videoFile: File,
//        title: String,
//        description: String,
//        tags: List<String>,
//        channelId: String,
//    ): String
//}
//
//internal class UploadVideoServiceImpl(
//    private val logger: Logger,
//    private val googleAuth: GoogleAuth,
//    private val httpTransport: HttpTransport,
//    private val jsonFactory: JsonFactory,
//) : UploadVideoService {
//    companion object {
//        const val QUOTA_COST = 1_600
//        private const val VIDEO_FILE_FORMAT = "video/*"
//        private const val APP_NAME = "youtube-cmdline-uploadvideo-sample"
//        private const val PRIVACY_STATUS_PUBLIC = "public"
//    }
//
//    // This OAuth 2.0 access scope allows an application to upload files
//    // to the authenticated user's YouTube channel, but doesn't allow
//    // other types of access.
//    private val scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload")
//
//    private var youtube: YouTube? = null
//
//    override fun uploadVideo(
//        config: String,
//        videoFile: File,
//        title: String,
//        description: String,
//        tags: List<String>,
//        channelId: String,
//    ): String {
//        logger.d { "Uploading: ${videoFile.path}" }
//
//        val credential = googleAuth.authorize(config)
//
//        youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
//            .setApplicationName(APP_NAME)
//            .build()
//            ?: error("Unable to create YouTube client")
//
//        val videoStatus = VideoStatus()
//        videoStatus.privacyStatus = PRIVACY_STATUS_PUBLIC
//
//        val snippet = VideoSnippet()
//        snippet.title = title
//        snippet.description = description
//        snippet.tags = tags
//        snippet.channelId = channelId
//
//        val video = Video()
//        video.status = videoStatus
//        video.setSnippet(snippet)
//
//        val mediaContent = InputStreamContent(VIDEO_FILE_FORMAT, videoFile.inputStream())
//
//        val videoInsert = youtube!!.videos()
//            .insert(listOf("snippet", "statistics", "status"), video, mediaContent)
//
//        val uploader = videoInsert.mediaHttpUploader
//        uploader.isDirectUploadEnabled = false
//
//        val progressListener = MediaHttpUploaderProgressListener { mediaHttpUploader ->
//            when (mediaHttpUploader.uploadState) {
//                MediaHttpUploader.UploadState.INITIATION_STARTED -> println("Initiation Started")
//                MediaHttpUploader.UploadState.INITIATION_COMPLETE -> println("Initiation Completed")
//                MediaHttpUploader.UploadState.MEDIA_IN_PROGRESS -> println("Upload in progress")
//                MediaHttpUploader.UploadState.MEDIA_COMPLETE -> println("Upload Completed!")
//                MediaHttpUploader.UploadState.NOT_STARTED -> println("Upload Not Started!")
//                null -> println("Upload state is null!")
//            }
//        }
//        uploader.progressListener = progressListener
//
//        val returnedVideo = try {
//            videoInsert.execute() ?: error("No video uploaded.")
//        } catch (e: GoogleJsonResponseException) {
//            val isQuotaError = e.details.errors.toString().contains("youtube.quota")
//            if (isQuotaError) error("QUOTA_EXCEEDED") else throw e
//        } catch (e: TokenResponseException) {
//            if (e.statusCode == 401) error("UNAUTHORIZED") else throw e
//        }
//
//        println("\n================== Returned Video ==================\n")
//        println("  - Id: " + returnedVideo.id)
//        println("  - Link: " + "https://www.youtube.com/watch?v=${returnedVideo.id}")
//        println("  - Title: " + returnedVideo.snippet.title)
//        println("  - Description: " + returnedVideo.snippet.description)
//        println("  - Tags: " + returnedVideo.snippet.tags)
//        println("  - Privacy Status: " + returnedVideo.status.privacyStatus)
//        println("  - Published at: " + returnedVideo.snippet.publishedAt)
//
//        return returnedVideo.id
//    }
//}
