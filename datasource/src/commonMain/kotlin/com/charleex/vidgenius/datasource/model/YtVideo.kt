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
    val thumbnailSmall: String,
    val thumbnailLarge: String,
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
        publishedAt = this.publishedAt?.let { Instant.fromEpochMilliseconds(it) },
    )
}
