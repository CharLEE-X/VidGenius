package com.charleex.vidgenius.datasource.feature.youtube

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Instant

internal class YoutubeRepositoryDebug() : YoutubeRepository {
    override val isFetchingUploads: StateFlow<Boolean> = flowOf(false) as StateFlow<Boolean>

    override val ytVideos: StateFlow<List<YtVideo>> = flowOf(
        listOf(
            YtVideo(
                id = "Sequence 02_1",
                title = "Video 1",
                description = "Description 1",
                tags = listOf("tag1", "tag2"),
                privacyStatus = "public",
                publishedAt = Instant.DISTANT_FUTURE
            ),
            YtVideo(
                id = "Sequence 02_2",
                title = "Video 1",
                description = "Description 1",
                tags = listOf("tag1", "tag2"),
                privacyStatus = "draft",
                publishedAt = Instant.DISTANT_FUTURE
            ),
            YtVideo(
                id = "Sequence 02_3",
                title = "Video 1",
                description = "Description 1",
                tags = listOf("tag1", "tag2"),
                privacyStatus = "public",
                publishedAt = Instant.DISTANT_FUTURE
            ),
            YtVideo(
                id = "Sequence 02_4",
                title = "Video 1",
                description = "Description 1",
                tags = listOf("tag1", "tag2"),
                privacyStatus = "draft",
                publishedAt = Instant.DISTANT_FUTURE
            ),
        )
    ).stateIn(
        CoroutineScope(Dispatchers.Default), SharingStarted.WhileSubscribed(), emptyList()
    )

    override fun startFetchUploads() {
    }

    override fun stopFetchUploads() {

    }

    override suspend fun updateVideo(ytVideo: YtVideo, video: Video): Boolean {
        return true
    }

    override fun signOut() {
    }
}
