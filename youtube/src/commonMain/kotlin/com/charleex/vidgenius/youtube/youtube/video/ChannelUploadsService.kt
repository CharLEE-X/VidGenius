package com.charleex.vidgenius.youtube.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.youtube.youtube.model.ChannelUploadsItem
import com.charleex.vidgenius.youtube.youtube.model.toUploadListItem
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Channel
import com.google.api.services.youtube.model.ChannelListResponse
import com.google.api.services.youtube.model.PlaylistItem
import kotlinx.datetime.Instant

/**
 * Print a list of videos uploaded to the authenticated user's YouTube channel.
 */
interface ChannelUploadsService {
    /**
     * Authorize the user, call the youtube.channels.list method to retrieve
     * the playlist ID for the list of videos uploaded to the user's channel,
     * and then call the youtube.playlistItems.list method to retrieve the
     * list of videos in that playlist.
     */
    suspend fun getUploadList(): List<ChannelUploadsItem>
    suspend fun getVideoDetail(videoId: String): ChannelUploadsItem
}

internal class ChannelUploadsServiceImpl(
    private val logger: Logger,
    private var youtube: YouTube,
) : ChannelUploadsService {
    override suspend fun getUploadList(): List<ChannelUploadsItem> {
        logger.d { "Getting upload list" }
        val channels = getChannelList() ?: error("Unable to get channel list.")
        val channelItems = channels.items
            ?: error("No channel items.")
        channelItems.ifEmpty { error("No channel items are empty.") }

        val uploadPlaylistId = channelItems.getDefaultChannelUploads()
            ?: error("Unable to get channel uploads.")

        val playlistItemRequest: YouTube.PlaylistItems.List =
            getPlaylistItemRequest(uploadPlaylistId)
                ?: error("Unable to create playlist item request.")

        return playlistItemRequest.getPlaylistItems()
    }

    override suspend fun getVideoDetail(videoId: String): ChannelUploadsItem {
        logger.d { "Getting video details for videoId: $videoId" }

        val videoList = youtube.videos()
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

    private fun YouTube.PlaylistItems.List.getPlaylistItems(): List<ChannelUploadsItem> {
        val playlistItemList = ArrayList<PlaylistItem>()

        var nextToken: String? = ""

        // Call the API one or more times to retrieve all items in the
        // list. As long as the API response returns a nextPageToken,
        // there are still more items to retrieve.
        do {
            pageToken = nextToken
            val playlistItemResult = execute()

            playlistItemList.addAll(playlistItemResult.items)

            nextToken = playlistItemResult.nextPageToken
        } while (nextToken != null)

        logger.d { "Found " + playlistItemList.size + " videos" }

        return playlistItemList.map { it.toUploadListItem() }
    }

    private fun getPlaylistItemRequest(uploadPlaylistId: String?): YouTube.PlaylistItems.List? {
        logger.d { "Retrieving playlist items" }
        val parts = listOf("id", "contentDetails", "snippet")
        val playlistItems = youtube.playlistItems().list(parts)
        playlistItems.playlistId = uploadPlaylistId
        // Only retrieve data used in this application, thereby making
        // the application more efficient. See:
        // https://developers.google.com/youtube/v3/getting-started#partial
        playlistItems.fields =
            "items(contentDetails/videoId,snippet/title,snippet/description,snippet/publishedAt),nextPageToken,pageInfo"
        return playlistItems
    }


    // The user's default channel is the first item in the list. Extract the playlist ID for the channel's videos
    // from the API response.
    private fun MutableList<Channel>.getDefaultChannelUploads(): String? {
        logger.d { "Getting default channel uploads" }
        return this[0].contentDetails.relatedPlaylists.uploads
    }

    private fun getChannelList(): ChannelListResponse? {
        logger.d { "Getting channel list" }
        // Call the API's channels.list method to retrieve the
        // resource that represents the authenticated user's channel.
        // In the API response, only include channel information needed for
        // this use case. The channel's contentDetails part contains
        // playlist IDs relevant to the channel, including the ID for the
        // list that contains videos uploaded to the channel.
        val channelRequest = youtube.channels().list(listOf("contentDetails"))
        channelRequest.mine = true
        channelRequest.fields = "items/contentDetails,nextPageToken,pageInfo"
        return channelRequest.execute()
    }
}
