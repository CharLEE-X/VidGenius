package com.charleex.vidgenius.feature.process_video_item

import com.charleex.vidgenius.feature.process_videos.model.UiVideo
import com.charleex.vidgenius.feature.process_videos.model.UiVideoCategory
import java.util.UUID

object ProcessVideoItemContract {
    data class State(
        val configId: String = UUID.randomUUID().toString(),
        val channelId: String = "UCjoFpbRmICEmDzE276LP59g",
        val numberOfScreenshots: Int = 3,
        val category: UiVideoCategory = UiVideoCategory(
            id = "1",
            name = "Animals",
        ),
        val uploadYouTube: Boolean = true,

        val uiVideo: UiVideo,
    )

    sealed interface Inputs {
        sealed interface Video : Inputs {
            object StartVideoProcessing : Video
            object CancelProcessingVideo : Video
        }

        sealed interface Config : Inputs {
            data class SetConfigId(val configId: String) : Video
            data class SetChannelId(val channelId: String) : Video
            data class SetNumberOfScreenshots(val numberOfScreenshots: Int) : Video
            data class SetCategory(val category: UiVideoCategory) : Video
            data class SetUploadYouTube(val uploadYouTube: Boolean) : Video
        }
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}
