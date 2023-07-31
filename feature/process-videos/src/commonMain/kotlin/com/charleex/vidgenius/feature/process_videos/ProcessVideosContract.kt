package com.charleex.vidgenius.feature.process_videos

import com.charleex.vidgenius.feature.process_videos.model.UiVideo

object ProcessVideosContract {
    data class State(
        val videos: List<UiVideo> = listOf(),
    )

    sealed interface Inputs {
        object ObserveVideos : Inputs
        data class SetVideos(val videos: List<UiVideo>) : Inputs
        data class DeleteVideoId(val videoId: String) : Inputs
        data class HandleFiles(val files: List<*>) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

