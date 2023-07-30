package com.charleex.vidgenius.feature.process_video.model

import com.charleex.vidgenius.datasource.db.Video
import kotlinx.datetime.Instant

data class UiVideo(
    val id: String,
    val path: String,
    val duration: Long?, // not used
    val screenshots: List<UiScreenshot> = emptyList(),
    val descriptionContext: String? = null,

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
        screenshots = this.screenshots.map { it.toUiScreenshot() },
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
