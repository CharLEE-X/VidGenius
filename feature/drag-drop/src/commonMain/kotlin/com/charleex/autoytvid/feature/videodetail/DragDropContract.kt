package com.charleex.autoytvid.feature.videodetail

import src.charleex.autoytvid.processor.model.Video

object DragDropContract {
    data class State(
        val videos: List<Video> = emptyList(),
        val loading: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class SetFiles(val videos: List<Video>) : Update
            data class SetLoading(val loading: Boolean) : Update
        }

        data class GetFiles(val anyList: List<*>) : Inputs
        data class DeleteFile(val video: Video) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

