package com.charleex.vidgenius.datasource.feature.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.privacyStatusFromString
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import java.io.IOException

interface MyUploadsService {
    suspend fun getUploadList(ytConfig: YtConfig): Flow<List<YtVideo>>
    suspend fun getVideoDetail(videoId: String): YtVideo
}

internal class MyUploadsServiceImpl(
    private val logger: Logger,
    private var googleAuth: GoogleAuth,
    private var httpTransport: HttpTransport,
    private var jsonFactory: JsonFactory,
) : MyUploadsService {
    companion object {
        const val QUOTA_COST = 50
        private const val APP_NAME = "youtube-cmdline-channeluploads-sample"
        val scopes = listOf("https://www.googleapis.com/auth/youtube.readonly")
    }

    private var youtube: YouTube? = null

    override suspend fun getUploadList(ytConfig: YtConfig): Flow<List<YtVideo>> = flow {
        try {
            logger.d { "Getting upload list" }
            val credential = googleAuth.authorize(ytConfig)

            val youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME)
                .build()
                ?: error("Unable to create YouTube object.")

            val channelResult = youtube.channels()
                .list(listOf("contentDetails"))
                .setMine(true)
                .setFields("items/contentDetails,nextPageToken,pageInfo")
                .execute()
                ?: error("Unable to get channel result.")

            val channelsList = channelResult.items ?: error("No channels found.")
            logger.d { "Channels: $channelsList" }

            val uploadPlaylistId = channelsList[0].contentDetails.relatedPlaylists.uploads
                ?: error("Unable to find upload playlist for channel.")

            val items = listOf(
                "contentDetails/videoId",
                "snippet/title",
                "snippet/description",
                "snippet/thumbnails",
                "snippet/publishedAt",
                "status/privacyStatus",
            ).joinToString(",")

            val playlistItemRequest = youtube.playlistItems()
                .list(listOf("id", "contentDetails", "snippet", "status"))
                .setPlaylistId(uploadPlaylistId)
                .setFields("items($items),nextPageToken,pageInfo")

            var nextToken: String? = ""
            val playlistItemList = mutableListOf<YtVideo>()
            do {
                playlistItemRequest.pageToken = nextToken
                val playlistItemResult = playlistItemRequest.execute()
                val items = playlistItemResult.items
                val uploadsChunk = items.map { playlistItem ->
                    val published = playlistItem.snippet.publishedAt.value
                    val instant = Instant.fromEpochMilliseconds(published)
                    val tags = getVideoTags(youtube, playlistItem.contentDetails.videoId)
                    val thumbnailUrl = playlistItem.snippet.thumbnails.default.url
                    YtVideo(
                        id = playlistItem.contentDetails.videoId,
                        title = playlistItem.snippet.title,
                        description = playlistItem.snippet.description,
                        tags = tags,
                        privacyStatus = privacyStatusFromString(playlistItem.status.privacyStatus),
                        thumbnailUrl = thumbnailUrl,
                        publishedAt = instant,
                    )
                }

                logger.d { "Uploads chunk: $uploadsChunk" }
                playlistItemList.addAll(uploadsChunk)
                emit(playlistItemList)

                nextToken = playlistItemResult.nextPageToken
            } while (nextToken != null)

            if (playlistItemList.isEmpty()) logger.e { "No videos found." }
            logger.d("Playlist Items: $playlistItemList")
        } catch (e: GoogleJsonResponseException) {
            logger.e(e) { "GoogleJsonResponseException code: ${e.statusCode} message: ${e.message}" }
            throw e
        } catch (e: IOException) {
            logger.e(e) { "IOException: ${e.message}" }
            throw e
        } catch (e: Throwable) {
            logger.e(e) { "Throwable: ${e.message}" }
            throw e
        }
    }

    private fun getVideoTags(youtube: YouTube, videoId: String): List<String> {
        val videoList = youtube.videos()
            .list(listOf("snippet", "contentDetails"))
            .setId(listOf(videoId))
            ?: error("Unable to create video list.")

        videoList.fields =
            "items(" +
                    "id," +
                    "snippet/tags," +
                    ")"

        val videoResult = videoList.execute()

        val videos = videoResult.items ?: error("Error getting videos.")
        return if (videos.isNotEmpty()) {
            val video: Video = videos[0]
            return video.snippet.tags ?: emptyList()
        } else emptyList()
    }

    override suspend fun getVideoDetail(videoId: String): YtVideo {
        logger.d { "Getting video details for videoId: $videoId" }

        val videoResult = youtube!!.videos()
            .list(listOf("snippet", "contentDetails"))
            .setId(listOf(videoId))
            .setFields(
                "items(" +
                        "id," +
                        "snippet/title," +
                        "snippet/description," +
                        "snippet/tags," +
                        "snippet/publishedAt," +
                        "status/privacyStatus," +
                        "contentDetails/duration" +
                        ")"
            )
            .execute()

        val videoItem = videoResult.items.firstOrNull { it.id == videoId }
            ?: error("Video with videoId: $videoId not found.")

        val title = videoItem.snippet.title ?: "No Title"
        val description = videoItem.snippet.description ?: "No Description"
        val tags = videoItem.snippet.tags ?: emptyList()
        val privacyStatus = videoItem.status.privacyStatus ?: "public"
        val thumbnailUrl = videoItem.snippet.thumbnails.default.url
        val publishedAt = Instant.fromEpochMilliseconds(videoItem.snippet.publishedAt.value)
        return YtVideo(
            id = videoId,
            title = title,
            description = description,
            tags = tags,
            privacyStatus = privacyStatusFromString(privacyStatus),
            thumbnailUrl = thumbnailUrl,
            publishedAt = publishedAt,
        )
    }
}
