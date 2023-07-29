package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.feature.videoscreenshots.model.UiVideo

object ScreenshotsToTextContract {
    data class State(
        val video: UiVideo = UiVideo(),
        val processing: Boolean = false,
        val progress: Float = 0f
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class Video(val video: UiVideo) : Update
            data class Processing(val isProcessing: Boolean) : Update
            data class Progress(val progress: Float) : Update
        }

        object Init : Inputs
        object ObserveVideo : Inputs
        object GetTextFromScreenshots : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

