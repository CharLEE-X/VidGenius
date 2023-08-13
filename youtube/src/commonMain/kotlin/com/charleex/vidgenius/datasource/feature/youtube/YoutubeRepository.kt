package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.YouTubeItem
import com.charleex.vidgenius.datasource.feature.youtube.model.toYouTubeItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface YoutubeRepository {
    suspend fun fetchUploads(config: String): Flow<List<YouTubeItem>>

    //    suspend fun updateVideo(ytVideo: YtVideo, localVideo: LocalVideo): Boolean
    fun signOut()
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val googleAuth: GoogleAuth,
    private val youTubeService: YouTubeService,
) : YoutubeRepository {

    override suspend fun fetchUploads(config: String): Flow<List<YouTubeItem>> {
        logger.d { "Getting channel uploads" }
        return youTubeService.getUploadList(config)
            .map { it.map { it.toYouTubeItem() } }
    }

//    override suspend fun updateVideo(ytVideo: YtVideo, localVideo: LocalVideo): Boolean {
//        val ytChannel = getChannel() ?: error("Channel cannot be null")
//        val hasContentInfoEnUS = localVideo.contentInfo.enUS.title.isNotEmpty() &&
//                localVideo.contentInfo.enUS.description.isNotEmpty()
//        if (!hasContentInfoEnUS) error("enUS content info cannot be empty")
//        val hasContentInfoPt = localVideo.contentInfo.pt.title.isNotEmpty() &&
//                localVideo.contentInfo.pt.description.isNotEmpty()
//        if (!hasContentInfoPt) error("pt content info cannot be empty")
//        val hasContentInfoEs = localVideo.contentInfo.es.title.isNotEmpty() &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        if (!hasContentInfoEs) error("es content info cannot be empty")
//        val hasContentInfoZh = localVideo.contentInfo.zh.title.isNotEmpty() &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        if (!hasContentInfoZh) error("zh content info cannot be empty")
//        val hasContentInfoHi = localVideo.contentInfo.hi.title.isNotEmpty() &&
//                localVideo.contentInfo.es.description.isNotEmpty()
//        if (!hasContentInfoHi) error("hi content info cannot be empty")
//        val hasTags = localVideo.contentInfo.tags.isNotEmpty()
//        if (!hasTags) error("Tags cannot be empty")
//        val tagsHaveText = localVideo.contentInfo.tags.all { it.isNotEmpty() }
//        if (!tagsHaveText) error("Tags cannot be empty")
//
//        logger.d("Updating video: ${localVideo.youtubeTitle} with:\n${localVideo.contentInfo.enUS.title}")
//
//        val result = updateVideoService.update(
//            ytConfig = ytChannel,
//            ytId = ytVideo.id,
//            contentInfo = localVideo.contentInfo,
//        )
//        return if (result != null) {
//            logger.d("Video updated successfully")
//            withContext(Dispatchers.IO) {
//                database.ytVideoQueries.delete(ytVideo.id)
//            }
//            true
//        } else {
//            logger.d("Video failed to update")
//            false
//        }
//        return false
//    }

    override fun signOut() {
        googleAuth.signOut("updatevideo")
    }
}

internal class YoutubeRepositoryDebug() : YoutubeRepository {

    override suspend fun fetchUploads(config: String): Flow<List<YouTubeItem>> {
        TODO("Not yet implemented")
    }

    override fun signOut() {
    }
}
