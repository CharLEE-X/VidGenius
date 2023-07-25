package src.charleex.vidgenius.whisper.model.transcription

import src.charleex.vidgenius.whisper.model.FileSource
import src.charleex.vidgenius.whisper.model.ModelId

internal class TranscriptionRequest(
    val audio: FileSource,
    val model: ModelId,
    val prompt: String? = null,
    val responseFormat: String? = null,
    val temperature: Double? = null,
    val language: String? = null,
)
