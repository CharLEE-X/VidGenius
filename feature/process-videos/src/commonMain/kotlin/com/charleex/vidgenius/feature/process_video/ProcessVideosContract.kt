package com.charleex.vidgenius.feature.process_video

object ProcessVideosContract {
    data class State(
        val videos: List<String> = listOf(),
    )

    sealed interface Inputs {
        object ObserveVideosIds : Inputs
        data class SetVideosIds(val videos: List<String>) : Inputs
        data class DeleteVideoId(val videoId: String) : Inputs
        data class HandleFiles(val files: List<*>) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

