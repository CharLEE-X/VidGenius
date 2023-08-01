package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.feature.process_videos.model.UiVideoCategory
import java.util.UUID

object ProcessVideosContract {
    data class State(
        val videos: List<UiVideo> = listOf(),

        val configId: String = UUID.randomUUID().toString(),
        val channelId: String = "UCmWNmg5PyF1VKUCttVcVzuw",
        val numberOfScreenshots: Int = 3,
        val category: Category = Category.ANIMALS,
        val uploadYouTube: Boolean = true,

        val queue: Set<String> = setOf(),
        val progress: Set<String> = setOf(),
        val done: Set<String> = setOf(),
        val canceled: Set<String> = setOf(),
        val failed: Set<String> = setOf(),
    )

    sealed interface Inputs {
        object ObserveVideos : Inputs
        data class SetVideos(val videos: List<UiVideo>) : Inputs
        data class DeleteVideoId(val videoId: String) : Inputs
        data class HandleFiles(val files: List<*>) : Inputs

        data class StartVideoProcessing(val videoId: String) : Inputs
        data class CancelProcessingVideo(val videoId: String) : Inputs

        data class SetChannelId(val channelId: String) : Inputs
        data class SetNumberOfScreenshots(val numberOfScreenshots: Int) : Inputs
        data class SetCategory(val category: Category) : Inputs
        data class SetUploadYouTube(val uploadYouTube: Boolean) : Inputs

        data class SetQueue(val queue: Set<String>) : Inputs
        data class SetProgress(val progress: Set<String>) : Inputs
        data class SetDone(val done: Set<String>) : Inputs
        data class SetCanceled(val canceled: Set<String>) : Inputs
        data class SetFailed(val failed: Set<String>) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

enum class Category {
    ANIMALS,
}
