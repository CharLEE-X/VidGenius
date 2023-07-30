package com.charleex.vidgenius.feature.process_video.model

sealed interface ProgressState {
    object None : ProgressState
    object Queued : ProgressState
    data class InProgress(val progress: Float) : ProgressState
    data class Success(val message: String?) : ProgressState
    data class Error(val message: String) : ProgressState
}
