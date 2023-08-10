package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.ytConfigs
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsService
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PrivacyStatus(val value: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    UNLISTED("unlisted")
}

interface YoutubeRepository {
    val ytVideos: StateFlow<List<YtVideo>>
    val isFetchingUploads: StateFlow<Boolean>
    suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean

    fun startFetchUploads()
    fun stopFetchUploads()

    fun signOut()
}

internal class YoutubeRepositoryImpl(
    private val logger: Logger,
    private val database: VidGeniusDatabase,
    private val googleAuth: GoogleAuth,
    private val myUploadsService: MyUploadsService,
    private val updateVideoService: UpdateVideoService,
    private val scope: CoroutineScope,
) : YoutubeRepository {
    private var fetchUploadsJob: Job = Job()

    private val _isFetchingUploads = MutableStateFlow(false)
    override val isFetchingUploads: StateFlow<Boolean> = _isFetchingUploads.asStateFlow()

    override val ytVideos: StateFlow<List<YtVideo>> = flowOfYtVideos()
        .stateIn(scope, SharingStarted.WhileSubscribed(), emptyList())

    private fun flowOfYtVideos(): Flow<List<YtVideo>> =
        database.ytVideoQueries.getAll().asFlow().map { it.executeAsList() }

    override fun startFetchUploads() {
        logger.d { "Getting channel uploads" }

        try {
            fetchUploadsJob = scope.launch {
                _isFetchingUploads.value = true
                val ytChannel = getChannel() ?: error("Channel cannot be null")
                myUploadsService.getUploadList(ytChannel).collect { myUploads ->
                    logger.d("Uploads: ${myUploads.size}")
                    val ytVideos = myUploads
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
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isFetchingUploads.value = false
        }
    }

    override fun stopFetchUploads() {
        logger.d("Stopping fetch uploads")
        _isFetchingUploads.value = false
        fetchUploadsJob.cancel()
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
            ytConfig = ytChannel,
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

    private fun getChannel(): YtConfig? {
        val config =
            database.configQueries.getAll().executeAsList().firstOrNull() ?: return null
        return ytConfigs.firstOrNull { it.id == config.ytConfig?.id }
    }
}
