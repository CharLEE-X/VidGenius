package com.charleex.vidgenius.feature.process_video.model

import com.charleex.vidgenius.datasource.VideoCategory
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.Screenshot
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class UiVideo(
    val id: String = "",
    val path: String = "",

    val screenshots: List<UiScreenshot> = emptyList(),

    val descriptions: List<String> = emptyList(),
    val descriptionContext: String? = null,

    val title: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),

    val youtubeVideoId: String? = null,

    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
)

internal fun Video.toUiVideo() = UiVideo(
    id = this.id,
    path = this.path,

    screenshots = this.screenshots.map { it.toUiScreenshot() },

    descriptions = this.descriptions,
    descriptionContext = this.descriptionContext,

    title = this.title,
    description = this.description,
    tags = this.tags,

    youtubeVideoId = this.youtubeVideoId,

    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
)

data class UiScreenshot(
    val id: String,
    val path: String,
)

private fun Screenshot.toUiScreenshot() = UiScreenshot(
    id = id,
    path = path,
)

data class UiVideoCategory(
    val id: String,
    val name: String,
)

internal fun VideoCategory.toUiVideoCategory() = UiVideoCategory(
    id = id,
    name = name,
)

internal fun UiVideoCategory.toVideoCategory() = VideoCategory(
    id = id,
    name = name,
)
