package com.charleex.vidgenius.datasource.feature.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.MyUploadsItem
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
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
    suspend fun getUploadList(ytConfig: YtConfig): Flow<List<MyUploadsItem>>
    suspend fun getVideoDetail(videoId: String): MyUploadsItem
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

    override suspend fun getUploadList(ytConfig: YtConfig): Flow<List<MyUploadsItem>> = flow {
        try {
            logger.d { "Getting upload list" }
            val credential = googleAuth.authorize(scopes, ytConfig)

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


            val playlistItemRequest = youtube.playlistItems()
                .list(listOf("id", "contentDetails", "snippet", "status"))
                ?: error("Unable to create playlist item list.")

            playlistItemRequest.playlistId = uploadPlaylistId

            val videoId = "contentDetails/videoId"
            val title = "snippet/title"
            val desc = "snippet/description"
            val publishedAt = "snippet/publishedAt"
            val status = "status/privacyStatus"
            playlistItemRequest.fields =
                "items($videoId,$title,$desc,$publishedAt,$status),nextPageToken,pageInfo"

            var nextToken: String? = ""
            val playlistItemList = mutableListOf<MyUploadsItem>()
            do {
                playlistItemRequest.pageToken = nextToken
                val playlistItemResult = playlistItemRequest.execute()
                val items = playlistItemResult.items
                val myUploads = items.map { playlistItem ->
                    val published = playlistItem.snippet.publishedAt.value
                    val instant = Instant.fromEpochMilliseconds(published)
                    val tags = getVideoTags(youtube, playlistItem.contentDetails.videoId)
                    MyUploadsItem(
                        ytId = playlistItem.contentDetails.videoId,
                        title = playlistItem.snippet.title,
                        description = playlistItem.snippet.description,
                        tags = tags,
                        privacyStatus = playlistItem.status.privacyStatus,
                        publishedAt = instant,
                    )
                }

                playlistItemList.addAll(myUploads)
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
            "items(id,snippet/title,snippet/description,snippet/publishedAt,contentDetails/duration)"

        val videoResult = videoList.execute()

        val videos = videoResult.items ?: error("Error getting videos.")
        if (videos.isNotEmpty()) {
            val video: Video = videos[0]
            return video.snippet.tags ?: emptyList()
        }
        return emptyList()
    }

    override suspend fun getVideoDetail(videoId: String): MyUploadsItem {
        logger.d { "Getting video details for videoId: $videoId" }

        val videoList = youtube!!.videos()
            .list(listOf("snippet", "contentDetails"))
            .setId(listOf(videoId))
            ?: error("Unable to create video list.")

        videoList.fields =
            "items(id,snippet/title,snippet/description,snippet/publishedAt,contentDetails/duration)"

        val videoResult = videoList.execute()
        val videoItem = videoResult.items.firstOrNull { it.id == videoId }
            ?: error("Video with videoId: $videoId not found.")

        val title = videoItem.snippet.title
        val description = videoItem.snippet.description
        val privacyStatus = videoItem.status.privacyStatus
        val publishedAt = videoItem.snippet.publishedAt
        val time = Instant.fromEpochMilliseconds(publishedAt.value)
        return MyUploadsItem(
            ytId = videoId,
            title = title,
            description = description,
            tags = error("Not implemented"),
            privacyStatus = privacyStatus,
            publishedAt = time
        )
    }
}
