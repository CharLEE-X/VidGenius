package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Channel
import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoLocalization
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

interface YouTubeService {
    fun getChannel(config: String): Channel
    suspend fun getUploadList(config: String): Flow<List<PlaylistItem>>
    suspend fun getVideoDetail(videoId: String, config: String): Video?

    fun update(
        config: String,
        ytId: String,
        title: String,
        description: String,
        tags: List<String>,
        localizations: Map<String, Pair<String, String>>,
        privacyStatus: String,
    ): Video?
}

internal class YouTubeServiceImpl(
    private val logger: Logger,
    private val googleAuth: GoogleAuth,
    private val httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
) : YouTubeService {
    companion object {
        private const val APP_NAME_UPDATE = "youtube-cmdline-updatevideo-sample"
        private const val APP_NAME = "youtube-cmdline-channeluploads-sample"
    }

    private var youtube: YouTube? = null

    override suspend fun getUploadList(
        config: String,
    ): Flow<List<PlaylistItem>> = flow {
        logger.d { "Getting upload list" }
        val channel = getChannel(config)
        val uploadPlaylistId = channel.contentDetails.relatedPlaylists.uploads
            ?: error("Unable to find upload playlist for channel.")

        val requestPlaylistItemsProperties = listOf(
            "contentDetails/videoId",
            "snippet/title",
            "snippet/description",
            "snippet/thumbnails",
            "snippet/publishedAt",
            "status/privacyStatus",
        ).joinToString(",")

        val playlistItemRequest = withYouTube(config).playlistItems()
            .list(
                listOf(
                    "id",
                    "contentDetails",
                    "snippet",
                    "status",
                )
            )
            .setPlaylistId(uploadPlaylistId)
            .setFields("items($requestPlaylistItemsProperties),nextPageToken,pageInfo")

        var nextToken: String? = ""
        do {
            playlistItemRequest.pageToken = nextToken
            val playlistItemResult = playlistItemRequest.execute()
            val items = playlistItemResult.items

            logger.d { "Uploads chunk: $items" }
            emit(items)

            nextToken = playlistItemResult.nextPageToken
        } while (nextToken != null)
    }

    private fun withYouTube(config: String): YouTube {
        logger.d { "Getting YouTube" }
        if (youtube != null) return youtube!!

        val credential = googleAuth.authorizeYouTube(config)

        youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(APP_NAME)
            .build()

        return youtube ?: error("YouTube not authorized")
    }

    override fun getChannel(config: String): Channel {
        logger.d { "Getting upload list" }
        val credential = googleAuth.authorizeYouTube(config)

        youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(APP_NAME)
            .build()
            ?: error("YouTube not authorized")

        val channelResult = withYouTube(config).channels()
            .list(listOf("contentDetails"))
            .setMine(true)
            .setFields("items/contentDetails,nextPageToken,pageInfo")
            .execute()
            ?: error("Unable to get channel result.")

        val channelsList = channelResult.items ?: error("No channels found.")
        logger.d { "Channels: $channelsList" }

        return channelsList[0] ?: error("Unable to get channel.")
    }

    override suspend fun getVideoDetail(videoId: String, config: String): Video? {
        logger.d { "Getting video details for videoId: $videoId" }

        val requestItems = listOf(
            "id",
            "snippet/title",
            "snippet/description",
            "snippet/tags",
            "snippet/publishedAt",
            "status/privacyStatus",
            "localizations",
            "contentDetails/duration",
            "status/rejectionReason",
            "statistics/likeCount",
            "statistics/dislikeCount",
            "statistics/viewCount",
            "statistics/commentCount",
            "statistics/favoriteCount",
        ).joinToString(",")

        val videoResult = withYouTube(config).videos()
            .list(
                listOf(
                    "id",
                    "snippet",
                    "contentDetails",
                    "status",
                    "localizations",
                    "statistics",
                )
            )
            .setId(listOf(videoId))
            .setFields("items($requestItems)")
            .execute()

        val video = videoResult.items.firstOrNull { it.id == videoId }
        logger.d { "Video: $video" }
        return video
    }

    override fun update(
        config: String,
        ytId: String,
        title: String,
        description: String,
        tags: List<String>,
        localizations: Map<String, Pair<String, String>>,
        privacyStatus: String,
    ): Video? {
        logger.d { "Updating video $ytId" }
        return try {
            val listResponse = withYouTube(config).videos()
                .list(listOf("snippet", "contentDetails", "status", "localizations", "statistics"))
                .setId(listOf(ytId))
                .execute()

            val videoList = listResponse.items
                .ifEmpty { error("Can't find a video with ID: $ytId") }

            val video = videoList[0]

            println("\n================== Video to Update ==================\n")
            println("  - ID: " + video.id)
            println("  - Title: " + video.snippet.title)
            println("  - Description: " + video.snippet.description)
            println("  - Tags: " + video.snippet.tags)
            println("  - PrivacyStatus: " + video.status.privacyStatus)
            println("  - Localizations: " + video.localizations)
            println("  - Channel title: " + video.snippet.channelTitle)
            println("  - RejectionReason: " + video.status.rejectionReason)
            println("  - LikeCount: " + video.statistics.likeCount)
            println("  - DislikeCount: " + video.statistics.dislikeCount)
            println("  - ViewCount: " + video.statistics.viewCount)
            println("  - CommentCount: " + video.statistics.commentCount)
            println("  - FavoriteCount: " + video.statistics.favoriteCount)
            println("  - Duration: " + video.contentDetails.duration)

            val multipleLocalizations = localizations.map { localization ->
                val videoLocalization = VideoLocalization()
                videoLocalization.title = localization.value.first
                videoLocalization.description = localization.value.second
                localization.key to videoLocalization
            }.toMap()

            video.localizations = multipleLocalizations

            val snippet = video.snippet
            snippet.title = title
            snippet.description = description
            snippet.tags = tags
            snippet.defaultLanguage = "en-US"

            video.snippet = snippet

            video.status.privacyStatus = privacyStatus

            val videoResponse = withYouTube(config).videos()
                .update(listOf("snippet", "status", "localizations"), video)
                .execute()
                ?: error("Can't update video with ID: $ytId")

            println("\n================== Returned Video ==================\n")
            println("  - ID: " + videoResponse.id)
            println("  - Title: " + videoResponse.snippet.title)
            println("  - Description: " + videoResponse.snippet.description)
            println("  - Tags: " + videoResponse.snippet.tags)
            println("  - PrivacyStatus: " + videoResponse.status.privacyStatus)
            println("  - Localizations: " + videoResponse.localizations)
            videoResponse.localizations.forEach {
                println("  - ${it.key} ${it.value}")
            }

            videoResponse
        } catch (e: GoogleJsonResponseException) {
            System.err.println(
                "GoogleJsonResponseException code: " + e.details.code + " : "
                        + e.details.message
            )
            e.printStackTrace()
            null
        } catch (e: IOException) {
            System.err.println("IOException: " + e.message)
            e.printStackTrace()
            null
        } catch (t: Throwable) {
            System.err.println("Throwable: " + t.message)
            t.printStackTrace()
            null
        }
    }
}