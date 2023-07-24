package com.charleex.autoytvid.feature.videolist

object VideoListContract {
    data class State(
        val list: List<VideoListItem> = emptyList(),
        val showLoader: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class SetList(val list: List<VideoListItem>) : Update
            data class ShowLoader(val showLoading: Boolean) : Update
        }

        object Init : Inputs
        object GetVideos : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

