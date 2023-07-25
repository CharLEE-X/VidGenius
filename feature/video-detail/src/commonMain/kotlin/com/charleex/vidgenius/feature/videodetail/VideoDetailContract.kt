package com.charleex.vidgenius.feature.videodetail

object VideoDetailContract {
    data class State(
        val videoDetail: VideoDetail = VideoDetail(),
        val loading: Boolean = false,
    )

    sealed interface Inputs {
        sealed interface Update : Inputs {
            data class SetDetail(val videoDetail: VideoDetail) : Update
            data class SetLoading(val loading: Boolean) : Update
        }

        data class Init(val videoId: String) : Inputs
        data class GetVideoDetail(val videoId: String) : Inputs
    }

    sealed interface Events {
        data class ShowError(val message: String) : Events
    }
}

