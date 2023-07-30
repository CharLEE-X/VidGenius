package com.charleex.vidgenius.feature.process_video

import com.charleex.vidgenius.feature.process_video.model.ProgressState
import com.charleex.vidgenius.feature.process_video.model.UiVideo

object ProcessVideoContract {
    data class State(
        val uiVideo: UiVideo? = null,
        val uiVideoState: ProgressState = ProgressState.None,

        val dragDropState: ProgressState = ProgressState.None,
        val isDragDropOen: Boolean = true,

        val screenshotsState: ProgressState = ProgressState.None,
        val isScreenshotsOpen: Boolean = false,
        val screenshotsQuantity: Int = 3,

        val descriptionState: ProgressState = ProgressState.None,
        val isDescriptionOpen: Boolean = false,

        val metaDataState: ProgressState = ProgressState.None,
        val isMetaDataOpen: Boolean = false,

        val uploadState: ProgressState = ProgressState.None,
        val isUploadOpen: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Video : Inputs {
            data class SetState(val uiVideoState: ProgressState) : Video
            data class SetUiVideo(val uiVideo: UiVideo?) : Video
            object ObserveUiVideo : Video
            object ProcessVideo : Video
        }

        sealed interface DragDrop : Inputs {
            data class SetState(val dragDropState: ProgressState) : DragDrop
            object ToggleIsOpen : DragDrop
            data class InitVideo(val files: List<*>) : DragDrop
            data class DeleteFile(val uiVideo: UiVideo) : DragDrop
        }

        sealed interface Screenshots : Inputs {
            data class SetState(val screenshotsState: ProgressState) : Screenshots
            object ToggleIsOpen : Screenshots
            data class ProcessScreenshots(val quantity: Int) : Screenshots
            data class DeleteScreenshot(val screenshotPath: String) : Screenshots
        }

        sealed interface Description : Inputs {
            data class SetState(val descriptionState: ProgressState) : Description
            object ToggleIsOpen : Description
            object GetDescription : Description
            object GetScreenshotContext : Description
        }

        sealed interface MetaData : Inputs {
            data class SetState(val metaState: ProgressState) : MetaData
            object ToggleIsOpen : MetaData
            object GetMeta : MetaData
        }

        sealed interface Upload : Inputs {
            data class SetState(val uploadState: ProgressState) : Upload
            object ToggleIsOpen : Upload
            object UploadVideo : Upload
        }
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

