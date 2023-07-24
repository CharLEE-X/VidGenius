package src.charleex.autoytvid.whisper

import okio.FileSystem
import okio.Path.Companion.toPath
import src.charleex.autoytvid.whisper.model.FileSource
import src.charleex.autoytvid.whisper.model.ModelId
import src.charleex.autoytvid.whisper.model.ResponseFormat
import src.charleex.autoytvid.whisper.model.translation.Translation
import src.charleex.autoytvid.whisper.model.translation.TranslationRequest

interface TranslationService {
    suspend fun translateAudio(
        /**
         * Formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm
         *
         * Example: "$resourcesPrefix/multilingual.wav"
         */
        stringPath: String,
        fileSystem: FileSystem = FileSystem.SYSTEM,
        prompt: String? = "Translate to english",
    ): Translation
}

internal class TranslationServiceImpl(
    private val audioService: AudioService,
    private val model: ModelId,
): TranslationService {
    override suspend fun translateAudio(
        /**
         * Formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm
         *
         * Example: "$resourcesPrefix/multilingual.wav"
         */
        stringPath: String,
        fileSystem: FileSystem,
        prompt: String?,
    ): Translation {
        val translationRequest = TranslationRequest(
            audio = FileSource(stringPath.toPath(), fileSystem),
            model = model,
            prompt = prompt,
            responseFormat = ResponseFormat.VERBOSE_JSON.name.lowercase(),
        )
        return audioService.translation(translationRequest)
    }
}
