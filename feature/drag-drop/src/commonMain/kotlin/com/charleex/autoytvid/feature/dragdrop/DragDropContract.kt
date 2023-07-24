package com.charleex.autoytvid.feature.dragdrop

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

        data class GetFiles(val anyList: List<*>) : Inputs
        data class DeleteFile(val dragDropItem: DragDropItem) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

