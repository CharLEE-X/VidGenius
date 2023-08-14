package com.charleex.vidgenius.datasource.model

import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.YouTubeItem
import com.charleex.vidgenius.datasource.feature.youtube.model.privacyStatusFromString
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class YtVideo(
    val id: String,
    val title: String,
    val description: String,
    val privacyStatus: PrivacyStatus,
    val tags: List<String>,
    val duration: String?,
    val localizations: Map<String, Pair<String, String>>,
    val thumbnailSmall: String,
    val thumbnailLarge: String,
    val likeCount: Int?,
    val dislikeCount: Int?,
    val viewCount: Int?,
    val commentCount: Int?,
    val publishedAt: Instant?,
)

internal fun YouTubeItem.toYoutubeVideo(): YtVideo {
    return YtVideo(
        id = this.id,
        title = this.title,
        description = this.description,
        privacyStatus = privacyStatusFromString(this.privacyStatus),
        tags = this.tags,
        thumbnailSmall = this.thumbnailSmall,
        thumbnailLarge = this.thumbnailLarge,
        duration = this.duration,
        localizations = this.localizations,
        likeCount = this.likeCount,
        dislikeCount = this.dislikeCount,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        publishedAt = this.publishedAt?.let { Instant.fromEpochMilliseconds(it) },
    )
}

internal fun YtVideo.toYouTubeItem(): YouTubeItem {
    return YouTubeItem(
        id = this.id,
        title = this.title,
        description = this.description,
        privacyStatus = this.privacyStatus.toString(),
        tags = this.tags,
        thumbnailSmall = this.thumbnailSmall,
        thumbnailLarge = this.thumbnailLarge,
        duration = this.duration,
        localizations = this.localizations,
        likeCount = this.likeCount,
        dislikeCount = this.dislikeCount,
        viewCount = this.viewCount,
        commentCount = this.commentCount,
        publishedAt = this.publishedAt?.toEpochMilliseconds(),
    )
}
