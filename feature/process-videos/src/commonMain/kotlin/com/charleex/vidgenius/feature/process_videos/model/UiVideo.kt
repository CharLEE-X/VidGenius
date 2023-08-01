package com.charleex.vidgenius.feature.process_videos.model

import com.charleex.vidgenius.datasource.VideoCategory
import com.charleex.vidgenius.datasource.db.Video
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class UiVideo(
    val id: String = "",
    val path: String = "",

    val screenshots: List<String> = emptyList(),

    val descriptions: List<String> = emptyList(),
    val descriptionContext: String? = null,

    val title: String? = null,
    val description: String? = null,
    val tags: List<String> = emptyList(),

    val youtubeVideoId: String? = null,

    val createdAt: Instant = Clock.System.now(),
    val modifiedAt: Instant = Clock.System.now(),
) {
    fun hasScreenshots(numberOfScreenshots: Int): Boolean {
        return screenshots.isNotEmpty() &&
                screenshots.all { it.isNotEmpty() } &&
                screenshots.size == numberOfScreenshots
    }

    fun hasDescriptions(numberOfDescriptions: Int): Boolean {
        return descriptions.isNotEmpty() &&
                descriptions.all { it.isNotEmpty() } &&
                descriptions.size == numberOfDescriptions
    }

    fun hasContext(): Boolean {
        return !descriptionContext.isNullOrEmpty()
    }

    fun hasMetadata(): Boolean {
        return !title.isNullOrEmpty() &&
                !description.isNullOrEmpty() &&
                tags.isNotEmpty() &&
                tags.all { it.isNotEmpty() }
    }

    fun hasYoutubeVideoId(): Boolean {
        return !youtubeVideoId.isNullOrEmpty()
    }
}

internal fun Video.toUiVideo() = UiVideo(
    id = this.id,
    path = this.path,

    screenshots = this.screenshots,

    descriptions = this.descriptions,
    descriptionContext = this.descriptionContext,

    title = this.title,
    description = this.description,
    tags = this.tags,

    youtubeVideoId = this.youtubeVideoId,

    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
)

data class UiVideoCategory(
    val id: String,
    val name: String,
)

fun VideoCategory.toUiVideoCategory() = UiVideoCategory(
    id = id,
    name = name,
)

fun UiVideoCategory.toVideoCategory() = VideoCategory(
    id = id,
    name = name,
)
