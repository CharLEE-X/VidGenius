package com.charleex.vidgenius.feature.process_video_item.model

import com.charleex.vidgenius.datasource.model.ProgressState

sealed interface UIProgressState {
    object None : UIProgressState
    object Queued : UIProgressState
    data class InProgress(val progress: Float) : UIProgressState
    object Success : UIProgressState
    data class Error(val message: String) : UIProgressState
    object Cancelled : UIProgressState
}

internal fun ProgressState.toUiProgressState() = when (this) {
    ProgressState.None -> UIProgressState.None
    ProgressState.Queued -> UIProgressState.Queued
    is ProgressState.InProgress -> UIProgressState.InProgress(progress)
    ProgressState.Success -> UIProgressState.Success
    is ProgressState.Error -> UIProgressState.Error(message)
    ProgressState.Cancelled -> UIProgressState.Cancelled
}

