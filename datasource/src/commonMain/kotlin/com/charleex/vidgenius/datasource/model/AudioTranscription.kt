package com.charleex.vidgenius.datasource.model

data class AudioTranscription(
    val id: Int,
    val originalText: String,
    val englishText: String,
    val language: String?,
    val transcriptionTime: Long,
    val translationTime: Long,
)
