package com.charleex.vidgenius.feature.videoscreenshots.model

import com.charleex.vidgenius.datasource.db.Video
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class UiVideo(
    val id: String = "",
    val path: String = "",
    val screenshots: List<UiScreenshot> = emptyList(),
    val duration: Long? = null,
    val title: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
)

internal fun Video.toUiVideo() = UiVideo(
    id = this.id,
    path = this.path,
    screenshots = this.screenshots.map { it.toUiScreenshot() },
    duration = this.duration,
    title = this.title,
    description = this.description,
    tags = this.tags,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
)

internal fun UiVideo.toVideo() = Video(
    id = this.id,
    path = this.path,
    screenshots = this.screenshots.map { it.toScreenshot() },
    duration = this.duration ?: 0,
    title = this.title,
    description = this.description,
    tags = this.tags,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
)
