package com.charleex.vidgenius.feature.dragdrop.model

sealed interface ProgressState {
    object None : ProgressState
    object Queued : ProgressState
    data class Loading(val progress: Float) : ProgressState
    data class Success(val message: String?) : ProgressState
    data class Error(val message: String) : ProgressState
}
