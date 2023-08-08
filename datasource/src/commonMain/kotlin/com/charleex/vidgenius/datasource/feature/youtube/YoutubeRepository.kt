package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.ChannelConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.MyUploadsItem
import com.charleex.vidgenius.datasource.feature.youtube.model.ytChannels
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsService
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

enum class PrivacyStatus(val value: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    UNLISTED("unlisted")
}

interface YoutubeRepository {
    val isFetchingUploads: StateFlow<Boolean>
    fun flowOfYtVideos(): Flow<List<YtVideo>>
    suspend fun fetchUploads()
    suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean
    fun signOut()
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val googleAuth: GoogleAuth,
    private val myUploadsService: MyUploadsService,
    private val updateVideoService: UpdateVideoService,
) : YoutubeRepository {
    private val _isFetchingUploads = MutableStateFlow(false)
    override val isFetchingUploads: StateFlow<Boolean> = _isFetchingUploads.asStateFlow()

    override fun flowOfYtVideos(): Flow<List<YtVideo>> =
        database.ytVideoQueries.getAll().asFlow().map { it.executeAsList() }

    override suspend fun fetchUploads() {
        logger.d { "Getting channel uploads" }
        _isFetchingUploads.update { true }
        val ytChannel = getChannel() ?: error("Channel cannot be null")
        val uploads: List<MyUploadsItem> = myUploadsService.getUploadList(ytChannel)
        logger.d("Uploads: ${uploads.size}")
        val ytVideos = uploads
            .filter {
                it.privacyStatus == PrivacyStatus.PRIVATE.value ||
                        it.privacyStatus == PrivacyStatus.UNLISTED.value
            }
            .map {
                YtVideo(
                    id = it.ytId,
                    title = it.title,
                    description = it.description,
                    tags = it.tags,
                    privacyStatus = it.privacyStatus,
                    publishedAt = it.publishedAt,
                )
            }
        logger.d("YtVideos: ${ytVideos.size}")

        withContext(Dispatchers.IO) {
            flowOfYtVideos().first().forEach {
                database.ytVideoQueries.delete(it.id)
            }
            ytVideos.forEach { ytVideo ->
                database.ytVideoQueries.upsert(ytVideo)
            }
        }
        _isFetchingUploads.update { false }
    }

    override suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean {
        val ytChannel = getChannel() ?: error("Channel cannot be null")
        val hasContentInfoEnUS = video.contentInfo.enUS.title.isNotEmpty() &&
                video.contentInfo.enUS.description.isNotEmpty()
        if (!hasContentInfoEnUS) error("enUS content info cannot be empty")
        val hasContentInfoPt = video.contentInfo.pt.title.isNotEmpty() &&
                video.contentInfo.pt.description.isNotEmpty()
        if (!hasContentInfoPt) error("pt content info cannot be empty")
        val hasContentInfoEs = video.contentInfo.es.title.isNotEmpty() &&
                video.contentInfo.es.description.isNotEmpty()
        if (!hasContentInfoEs) error("es content info cannot be empty")
        val hasContentInfoZh = video.contentInfo.zh.title.isNotEmpty() &&
                video.contentInfo.es.description.isNotEmpty()
        if (!hasContentInfoZh) error("zh content info cannot be empty")
        val hasContentInfoHi = video.contentInfo.hi.title.isNotEmpty() &&
                video.contentInfo.es.description.isNotEmpty()
        if (!hasContentInfoHi) error("hi content info cannot be empty")
        val hasTags = video.contentInfo.tags.isNotEmpty()
        if (!hasTags) error("Tags cannot be empty")
        val tagsHaveText = video.contentInfo.tags.all { it.isNotEmpty() }
        if (!tagsHaveText) error("Tags cannot be empty")

        logger.d("Updating video: ${video.youtubeTitle} with:\n${video.contentInfo.enUS.title}")

        val result = updateVideoService.update(
            channelConfig = ytChannel,
            ytId = ytVideo.id,
            contentInfo = video.contentInfo,
        )
        return if (result != null) {
            logger.d("Video updated successfully")
            withContext(Dispatchers.IO) {
                database.ytVideoQueries.delete(ytVideo.id)
            }
            true
        } else {
            logger.d("Video failed to update")
            false
        }
    }

    override fun signOut() {
        googleAuth.signOut("updatevideo")
    }

    private fun getChannel(): ChannelConfig? {
        val config = database.configQueries.getAll().executeAsList().firstOrNull() ?: return null
        return ytChannels.firstOrNull { it.id == config.channelConfig?.id }
    }
}
