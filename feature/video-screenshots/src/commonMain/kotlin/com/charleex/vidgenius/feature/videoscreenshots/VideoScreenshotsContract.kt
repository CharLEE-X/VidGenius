package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.feature.videoscreenshots.model.UiVideo

object VideoScreenshotsContract {
    data class State(
        val video: UiVideo = UiVideo(),
        val percentages: List<Double> = listOf(0.25, 0.5, 0.75),
        val processing: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class Video(val video: UiVideo) : Update
            data class Percentages(val percentages: List<Double>) : Update
            data class Processing(val isProcessing: Boolean) : Update
        }

        object Init : Inputs
        object CaptureScreenshots : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

