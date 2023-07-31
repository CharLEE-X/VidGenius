package com.charleex.vidgenius.datasource.model

sealed interface ProgressState {
    object Queued : ProgressState
    data class InProgress(val progress: Float) : ProgressState
    object Success : ProgressState
    data class Error(val message: String) : ProgressState
    object Cancelled : ProgressState
}
