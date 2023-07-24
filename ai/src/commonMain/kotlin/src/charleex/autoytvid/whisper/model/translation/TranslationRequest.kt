package src.charleex.autoytvid.whisper.model.translation

import src.charleex.autoytvid.whisper.model.FileSource
import src.charleex.autoytvid.whisper.model.ModelId

internal class TranslationRequest(
    val audio: FileSource,
    val model: ModelId,
    val prompt: String? = null,
    val responseFormat: String? = null,
    val temperature: Double? = null,
)
