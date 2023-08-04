package com.charleex.vidgenius.datasource.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ProgressState {
    object Queued : ProgressState
    data class InProgress(val progress: Float) : ProgressState
    object Success : ProgressState
    data class Error(val message: String) : ProgressState
    object Cancelled : ProgressState
}
