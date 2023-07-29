package com.charleex.vidgenius.feature.dragdrop.model

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.Screenshot
import kotlinx.datetime.Instant

data class UiVideo(
    val id: String,
    val path: String,
    val duration: Long?,
    val screenshots: List<Screenshot> = emptyList(),
    val title: String?,
    val description: String?,
    val tags: List<String> = emptyList(),
    val createdAt: Instant? = null,
    val modifiedAt: Instant? = null,
)

fun Video.toUiVideo(): UiVideo {
    return UiVideo(
        id = this.id,
        path = this.path,
        duration = this.duration,
        screenshots = this.screenshots,
        title = this.title,
        description = this.description,
        tags = this.tags,
        createdAt = this.createdAt,
        modifiedAt = this.modifiedAt,
    )
}

fun UiVideo.video(): Video {
    return Video(
        id = this.id,
        path = this.path,
        duration = this.duration ?: 0,
        screenshots = emptyList(),
        title = null,
        description = null,
        tags = emptyList(),
        createdAt = this.createdAt ?: Instant.DISTANT_PAST,
        modifiedAt = this.modifiedAt ?: Instant.DISTANT_PAST,
    )
}
