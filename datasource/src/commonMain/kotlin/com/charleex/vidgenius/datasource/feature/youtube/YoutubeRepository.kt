package com.charleex.vidgenius.datasource.feature.youtube

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.MyUploadsItem
import com.charleex.vidgenius.datasource.feature.youtube.video.MyUploadsService
import com.charleex.vidgenius.datasource.feature.youtube.video.UpdateVideoService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class PrivacyStatus(val value: String) {
    PUBLIC("public"),
    PRIVATE("private"),
    UNLISTED("unlisted")
}

interface YoutubeRepository {
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
    override fun flowOfYtVideos(): Flow<List<YtVideo>> =
        database.ytVideoQueries.getAll().asFlow().map { it.executeAsList() }

    override suspend fun fetchUploads() {
        logger.d { "Getting channel uploads" }
        val uploads: List<MyUploadsItem> = myUploadsService.getUploadList()
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
    }

    override suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean {
        val title = video.title ?: error("Title cannot be null")
        val description = video.description ?: error("Description cannot be null")
        val tags = video.tags.ifEmpty { error("Tags cannot be empty") }

        logger.d(
            "Updating video: ${video.youtubeId} with:\n${title}, \n" +
                    "$tags, \n${description}"
        )
        val result = updateVideoService.update(
            ytId = ytVideo.id,
            title = title,
            description = description,
            tags = tags,
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
        googleAuth.signout("updatevideo")
    }
}
