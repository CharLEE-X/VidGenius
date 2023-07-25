package com.charleex.vidgenius.feature.videoscreenshots

import java.io.File

object VideoScreenshotsContract {
    data class State(
        val name: String = "",
        val path: String = "",
        val duration: String = "",
        val screenshots: List<File> = emptyList(),
        val processing: Boolean = false,
        val percentages: List<Double> = listOf(0.25, 0.5, 0.75),
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class Name(val name: String) : Update
            data class Path(val path: String) : Update
            data class Duration(val duration: String) : Update
            data class Screenshots(val images: List<File>) : Update
            data class Processing(val processing: Boolean) : Update
        }

        object Init : Inputs
        object GetScreenshots : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

