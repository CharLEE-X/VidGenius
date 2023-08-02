package com.charleex.vidgenius.datasource.youtube.model

import com.google.api.services.youtube.model.PlaylistItem
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoContentDetails
import com.google.api.services.youtube.model.VideoSnippet
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
