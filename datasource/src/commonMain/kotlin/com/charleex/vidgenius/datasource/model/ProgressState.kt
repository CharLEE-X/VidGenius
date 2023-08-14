package com.charleex.vidgenius.datasource.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface ProgressState {
    @Serializable
    object Queued : ProgressState

    @Serializable
    data class InProgress(val progress: Float) : ProgressState

    @Serializable
    object Success : ProgressState

    @Serializable
    data class Error(val message: String) : ProgressState

    @Serializable
    object Cancelled : ProgressState
}
