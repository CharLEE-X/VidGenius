package com.charleex.vidgenius.feature.videoscreenshots

import com.charleex.vidgenius.feature.videoscreenshots.model.UiVideo

object VideoScreenshotsContract {
    data class State(
        val video: UiVideo = UiVideo(),
        val timestamps: List<Long> = listOf(),
        val processing: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class Video(val video: UiVideo) : Update
            data class Timestamps(val timestamps: List<Long>) : Update
            data class Processing(val isProcessing: Boolean) : Update
        }

        object Init : Inputs
        object ObserveVideo : Inputs
        object GetTimestamps : Inputs
        object CaptureScreenshots : Inputs

        data class SaveTimestamp(val timestamp: Long) : Inputs
        data class DeleteTimestamp(val timestamp: Long) : Inputs

        data class OnScreenshotNotExist(val screenshotPath: String) : Inputs
        data class DeleteScreenshot(val screenshotId: String) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

