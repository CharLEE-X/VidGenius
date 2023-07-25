package com.charleex.vidgenius.feature.dragdrop

import com.charleex.vidgenius.feature.dragdrop.model.DragDropItem

object DragDropContract {
    data class State(
        val dragDropItems: List<DragDropItem> = emptyList(),
        val loading: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class SetFiles(val dragDropItems: List<DragDropItem>) : Update
            data class SetLoading(val loading: Boolean) : Update
        }

        object ObserveFiles : Inputs
        data class GetFiles(val anyList: List<*>) : Inputs
        data class DeleteFile(val dragDropItem: DragDropItem) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

