//package com.charleex.vidgenius.datasource.feature.youtube.video
//
//import co.touchlab.kermit.Logger
//import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
//import com.google.api.client.http.HttpTransport
//import com.google.api.client.json.JsonFactory
//import com.google.api.services.youtube.YouTube
//import com.google.api.services.youtube.model.PlaylistItem
//import com.google.api.services.youtube.model.Video
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.flow
//
//interface MyUploadsService {
//    suspend fun getUploadList(config: String): Flow<List<PlaylistItem>>
//    suspend fun getVideoDetail(videoId: String): Video?
//}
//
//internal class MyUploadsServiceImpl(
//    private val logger: Logger,
//    private var googleAuth: GoogleAuth,
//    private var httpTransport: HttpTransport,
//    private var jsonFactory: JsonFactory,
//) : MyUploadsService {
//    companion object {
//        private const val APP_NAME = "youtube-cmdline-channeluploads-sample"
//    }
//
//    private var youtube: YouTube? = null
//
//    override suspend fun getUploadList(
//        config: String,
//    ): Flow<List<PlaylistItem>> = flow {
//        logger.d { "Getting upload list" }
//        val credential = googleAuth.authorizeYouTube(config)
//
//        val youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
//            .setApplicationName(APP_NAME)
//            .build()
//            ?: error("Unable to create YouTube object.")
//
//        val channelResult = youtube.channels()
//            .list(listOf("contentDetails"))
//            .setMine(true)
//            .setFields("items/contentDetails,nextPageToken,pageInfo")
//            .execute()
//            ?: error("Unable to get channel result.")
//
//        val channelsList = channelResult.items ?: error("No channels found.")
//        logger.d { "Channels: $channelsList" }
//
//        val uploadPlaylistId = channelsList[0].contentDetails.relatedPlaylists.uploads
//            ?: error("Unable to find upload playlist for channel.")
//
//        val requestItems = listOf(
//            "contentDetails/videoId",
//            "contentDetails/duration",
//            "snippet/title",
//            "snippet/description",
//            "snippet/tags",
//            "snippet/thumbnails",
//            "snippet/publishedAt",
//            "status/privacyStatus",
//        ).joinToString(",")
//
//        val playlistItemRequest = youtube.playlistItems()
//            .list(listOf("id", "contentDetails", "snippet", "status"))
//            .setPlaylistId(uploadPlaylistId)
//            .setFields("items($requestItems),nextPageToken,pageInfo")
//
//        var nextToken: String? = ""
//        val playlistItemList = mutableListOf<PlaylistItem>()
//        do {
//            playlistItemRequest.pageToken = nextToken
//            val playlistItemResult = playlistItemRequest.execute()
//            val items = playlistItemResult.items
//
//            logger.d { "Uploads chunk: $items" }
//            playlistItemList.addAll(items)
//            emit(playlistItemList)
//
//            nextToken = playlistItemResult.nextPageToken
//        } while (nextToken != null)
//
//        if (playlistItemList.isEmpty()) logger.e { "No videos found." }
//        logger.d("Playlist Items: $playlistItemList")
//    }
//
//    private fun getVideoTags(youtube: YouTube, videoId: String): List<String> {
//        val videoList = youtube.videos()
//            .list(listOf("snippet", "contentDetails"))
//            .setId(listOf(videoId))
//            ?: error("Unable to create video list.")
//
//        val requestItems = listOf(
//            "id",
//            "snippet/tags",
//        ).joinToString(",")
//
//        videoList.fields = "items($requestItems)"
//
//        val videoResult = videoList.execute()
//
//        val videos = videoResult.items ?: error("Error getting videos.")
//        return if (videos.isNotEmpty()) {
//            val video: Video = videos[0]
//            return video.snippet.tags ?: emptyList()
//        } else emptyList()
//    }
//
//    override suspend fun getVideoDetail(videoId: String): Video? {
//        logger.d { "Getting video details for videoId: $videoId" }
//
//
//        val requestItems = listOf(
//            "id",
//            "snippet/title",
//            "snippet/description",
//            "snippet/tags",
//            "snippet/tags",
//            "snippet/publishedAt",
//            "status/privacyStatus",
//            "contentDetails/duration",
//        ).joinToString(",")
//
//        val videoResult = youtube!!.videos()
//            .list(listOf("snippet", "contentDetails", "status"))
//            .setId(listOf(videoId))
//            .setFields("items($requestItems)")
//            .execute()
//
//        return videoResult.items.firstOrNull { it.id == videoId }
//    }
//}
