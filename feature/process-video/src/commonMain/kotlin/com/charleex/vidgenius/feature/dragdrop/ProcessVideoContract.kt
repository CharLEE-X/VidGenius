package com.charleex.vidgenius.feature.dragdrop

import com.charleex.vidgenius.feature.dragdrop.model.ProgressState
import com.charleex.vidgenius.feature.dragdrop.model.UiVideo

object ProcessVideoContract {
    data class State(
        val uiVideo: UiVideo? = null,
        val uiVideoState: ProgressState = ProgressState.None,
        val dragDropState: ProgressState = ProgressState.None,
        val screenshotsState: ProgressState = ProgressState.None,
        val descriptionState: ProgressState = ProgressState.None,
        val metaState: ProgressState = ProgressState.None,
        val uploadState: ProgressState = ProgressState.None,
    )

    sealed interface Inputs {
        sealed interface DragDrop : Inputs {
            data class SetState(val dragDropState: ProgressState) : DragDrop
            data class InitVideo(val files: List<*>) : DragDrop
            data class DeleteFile(val uiVideo: UiVideo) : DragDrop
        }

        sealed interface Screenshots : Inputs {
            data class SetState(val screenshotsState: ProgressState) : Screenshots
            data class GetScreenshots(val quantity: Int = 3) : Screenshots
        }

        sealed interface Description : Inputs {
            data class SetState(val descriptionState: ProgressState) : Description
            data class GetDescription(val uiVideo: UiVideo) : Description
        }

        sealed interface MeatData : Inputs {
            data class SetState(val metaState: ProgressState) : MeatData
            data class GetMeta(val uiVideo: UiVideo) : MeatData
        }

        sealed interface Upload : Inputs {
            data class SetState(val uploadState: ProgressState) : Upload
            object UploadVideo : Upload
        }

        sealed interface Video : Inputs {
            data class SetState(val uiVideoState: ProgressState) : Video
            data class SetUiVideo(val uiVideo: UiVideo?) : Video
            object ObserveUiVideo : Video
        }
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

