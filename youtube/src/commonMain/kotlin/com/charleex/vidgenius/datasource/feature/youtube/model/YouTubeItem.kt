package com.charleex.vidgenius.datasource.feature.youtube.model

import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.Video

data class YouTubeItem(
    val id: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val privacyStatus: String,
    val localizations: Map<String, Pair<String, String>>,
    val duration: String?,
    val channelTitle: String,
    val rejectionReason: String? = null,
    val likeCount: Int? = null,
    val dislikeCount: Int? = null,
    val viewCount: Int? = null,
    val commentCount: Int? = null,
    val favoriteCount: Int? = null,
)

internal fun Video.toYouTubeItem() = YouTubeItem(
    id = id,
    title = snippet.title,
    description = snippet.description,
    tags = snippet.tags,
    privacyStatus = status.privacyStatus,
    localizations = localizations.map { (key, value) ->
        key to (value.title to value.description)
    }.toMap(),
    duration = contentDetails.duration,
    channelTitle = snippet.channelTitle,
    rejectionReason = status.rejectionReason,
    likeCount = statistics.likeCount.toInt(),
    dislikeCount = statistics.dislikeCount.toInt(),
    viewCount = statistics.viewCount.toInt(),
    commentCount = statistics.commentCount.toInt(),
    favoriteCount = statistics.favoriteCount.toInt(),
)

internal fun PlaylistItem.toYouTubeItem() = YouTubeItem(
    id = id,
    title = snippet.title,
    description = snippet.description,
    tags = emptyList(),
    privacyStatus = status.privacyStatus,
    localizations = emptyMap(),
    duration = null,
    channelTitle = snippet.channelTitle,
    rejectionReason = null,
    likeCount = null,
    dislikeCount = null,
    viewCount = null,
    commentCount = null,
    favoriteCount = null,
)
