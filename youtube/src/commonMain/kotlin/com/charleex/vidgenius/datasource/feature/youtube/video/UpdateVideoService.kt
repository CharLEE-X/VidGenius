//package com.charleex.vidgenius.datasource.feature.youtube.video
//
//import co.touchlab.kermit.Logger
//import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
//import com.google.api.client.googleapis.json.GoogleJsonResponseException
//import com.google.api.client.http.HttpTransport
//import com.google.api.client.json.JsonFactory
//import com.google.api.services.youtube.model.Video
//import com.google.api.services.youtube.model.VideoLocalization
//import java.io.IOException
//
//internal interface UpdateVideoService {
//    fun update(
//        config: String,
//        ytId: String,
//        title: String,
//        description: String,
//        tags: List<String>,
//        localizations: Map<String, Pair<String, String>>,
//        privacyStatus: String,
//    ): Video?
//}
//
//internal class UpdateVideoServiceImpl(
//    private val logger: Logger,
//    private val googleAuth: GoogleAuth,
//    private var httpTransport: HttpTransport,
//    private val jsonFactory: JsonFactory,
//) : UpdateVideoService {
//    companion object {
//        private const val APP_NAME = "youtube-cmdline-updatevideo-sample"
//    }
//
//    override fun update(
//        config: String,
//        ytId: String,
//        title: String,
//        description: String,
//        tags: List<String>,
//        localizations: Map<String, Pair<String, String>>,
//        privacyStatus: String,
//    ): Video? {
//        logger.d { "Updating video $ytId" }
//        return try {
//            val youtube = googleAuth.authorizeYouTube(config)
//                ?: error("YouTube not authorized")
//
//            val listResponse = youtube.videos()
//                .list(listOf("snippet", "contentDetails", "status", "localizations", "statistics"))
//                .setId(listOf(ytId))
//                .execute()
//
//            val videoList = listResponse.items
//                .ifEmpty { error("Can't find a video with ID: $ytId") }
//
//            val video = videoList[0]
//
//            println("\n================== Video to Update ==================\n")
//            println("  - ID: " + video.id)
//            println("  - Title: " + video.snippet.title)
//            println("  - Description: " + video.snippet.description)
//            println("  - Tags: " + video.snippet.tags)
//            println("  - PrivacyStatus: " + video.status.privacyStatus)
//            println("  - Localizations: " + video.localizations)
//            println("  - Channel title: " + video.snippet.channelTitle)
//            println("  - RejectionReason: " + video.status.rejectionReason)
//            println("  - LikeCount: " + video.statistics.likeCount)
//            println("  - DislikeCount: " + video.statistics.dislikeCount)
//            println("  - ViewCount: " + video.statistics.viewCount)
//            println("  - CommentCount: " + video.statistics.commentCount)
//            println("  - FavoriteCount: " + video.statistics.favoriteCount)
//            println("  - Duration: " + video.contentDetails.duration)
//            println("  - CategoryId: " + video.snippet.categoryId)
//
//            val multipleLocalizations = localizations.map { localization ->
//                val videoLocalization = VideoLocalization()
//                videoLocalization.title = localization.value.first
//                videoLocalization.description = localization.value.second
//                localization.key to videoLocalization
//            }.toMap()
//
//            video.localizations = multipleLocalizations
//
//            val snippet = video.snippet
//            snippet.title = title
//            snippet.description = description
//            snippet.tags = tags
//            snippet.defaultLanguage = "en-US"
//
//            video.snippet = snippet
//
//            video.status.privacyStatus = privacyStatus
//
//            val videoResponse = youtube!!.videos()
//                .update(listOf("snippet", "status", "localizations"), video)
//                .execute()
//                ?: error("Can't update video with ID: $ytId")
//
//            println("\n================== Returned Video ==================\n")
//            println("  - ID: " + videoResponse.id)
//            println("  - Title: " + videoResponse.snippet.title)
//            println("  - Description: " + videoResponse.snippet.description)
//            println("  - Tags: " + videoResponse.snippet.tags)
//            println("  - PrivacyStatus: " + videoResponse.status.privacyStatus)
//            println("  - Localizations: " + videoResponse.localizations)
//            videoResponse.localizations.forEach {
//                println("  - ${it.key} ${it.value}")
//            }
//
//            videoResponse
//        } catch (e: GoogleJsonResponseException) {
//            System.err.println(
//                "GoogleJsonResponseException code: " + e.details.code + " : "
//                        + e.details.message
//            )
//            e.printStackTrace()
//            null
//        } catch (e: IOException) {
//            System.err.println("IOException: " + e.message)
//            e.printStackTrace()
//            null
//        } catch (t: Throwable) {
//            System.err.println("Throwable: " + t.message)
//            t.printStackTrace()
//            null
//        }
//    }
//}
