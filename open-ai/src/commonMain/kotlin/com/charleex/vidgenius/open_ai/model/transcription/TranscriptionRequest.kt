package com.charleex.vidgenius.open_ai.model.transcription

internal class TranscriptionRequest(
    val audio: FileSource,
    val model: ModelId,
    val prompt: String? = null,
    val responseFormat: String? = null,
    val temperature: Double? = null,
    val language: String? = null,
)
