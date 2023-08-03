package com.charleex.vidgenius.youtube.model

import com.google.api.services.youtube.model.PlaylistItem
import kotlinx.datetime.Instant

data class ChannelUploadsItem(
    val videoId: String,
    val title: String,
    val description: String? = null,
    val publishedAt: Instant = Instant.DISTANT_PAST,
)

internal fun PlaylistItem.toUploadListItem(): ChannelUploadsItem {
    return ChannelUploadsItem(
        videoId = contentDetails.videoId,
        title = snippet.title,
        description = snippet.description,
        publishedAt = Instant.fromEpochMilliseconds(snippet.publishedAt.value),
    )
}
