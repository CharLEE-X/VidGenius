package com.charleex.vidgenius.datasource.model

import kotlinx.datetime.Clock

data class Message(
    val id: Int,
    val message: String,
    val language: String?,
    val role: Role,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val answerTime: Long,
)
