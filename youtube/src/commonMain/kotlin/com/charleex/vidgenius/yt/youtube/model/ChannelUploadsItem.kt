package com.charleex.vidgenius.yt.youtube.model

import com.google.api.services.youtube.model.PlaylistItem
import kotlinx.datetime.Instant

data class ChannelUploadsItem(
    val videoId: String,
    val title: String = "no title",
    val description: String = "no description",
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
