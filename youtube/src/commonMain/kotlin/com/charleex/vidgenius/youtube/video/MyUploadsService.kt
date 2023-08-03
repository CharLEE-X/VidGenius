package com.charleex.vidgenius.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.youtube.auth.GoogleAuth
import com.charleex.vidgenius.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.youtube.model.toUploadListItem
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.PlaylistItem
import kotlinx.datetime.Instant
import java.io.IOException

/**
 * Print a list of videos uploaded to the authenticated user's YouTube channel.
 */
interface MyUploadsService {
    /**
     * Authorize the user, call the youtube.channels.list method to retrieve
     * the playlist ID for the list of videos uploaded to the user's channel,
     * and then call the youtube.playlistItems.list method to retrieve the
     * list of videos in that playlist.
     */
    suspend fun getUploadList(): List<ChannelUploadsItem>
    suspend fun getVideoDetail(videoId: String): ChannelUploadsItem
}

internal class MyUploadsServiceImpl(
    private val logger: Logger,
    private var googleAuth: GoogleAuth,
    private var httpTransport: HttpTransport,
    private var jsonFactory: JsonFactory,
) : MyUploadsService {
    companion object {
        const val QUOTA_COST = 50
        private const val STORE = "channeluploads"
        private const val APP_NAME = "youtube-cmdline-channeluploads-sample"
    }

    private val scopes = listOf("https://www.googleapis.com/auth/youtube.readonly")
    private var youtube: YouTube? = null

    override suspend fun getUploadList(): List<ChannelUploadsItem> {
        logger.d { "Getting upload list" }
        try {
            val credential = googleAuth.authorize(scopes, STORE)

            val youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME)
                .build()

            val channelRequest = youtube!!.channels().list(listOf("contentDetails"))
            channelRequest.mine = true
            channelRequest.fields = "items/contentDetails,nextPageToken,pageInfo"
            val channelResult = channelRequest.execute()

            val channelsList = channelResult.items
                ?: error("No channels found.")

            val uploadPlaylistId = channelsList[0].contentDetails.relatedPlaylists.uploads
                ?: error("Unable to find upload playlist for channel.")

            val playlistItemList = mutableListOf<PlaylistItem>()

            val playlistItemRequest = youtube.playlistItems()
                .list(listOf("id", "contentDetails", "snippet"))
                ?: error("Unable to create playlist item list.")

            playlistItemRequest.playlistId = uploadPlaylistId
            playlistItemRequest.fields =
                "items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo"

            var nextToken: String? = ""

            do {
                playlistItemRequest.pageToken = nextToken
                val playlistItemResult = playlistItemRequest.execute()

                playlistItemList.addAll(playlistItemResult.items)

                nextToken = playlistItemResult.nextPageToken
            } while (nextToken != null)

            return playlistItemList.map { it.toUploadListItem() }
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

    override suspend fun getVideoDetail(videoId: String): ChannelUploadsItem {
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
        val publishedAt = videoItem.snippet.publishedAt
        val time = Instant.fromEpochMilliseconds(publishedAt.value)
        return ChannelUploadsItem(videoId, title, description, time)
    }
}
