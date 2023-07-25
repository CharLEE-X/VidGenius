package src.charleex.vidgenius.whisper

import okio.FileSystem
import okio.Path.Companion.toPath
import src.charleex.vidgenius.whisper.model.FileSource
import src.charleex.vidgenius.whisper.model.ModelId
import src.charleex.vidgenius.whisper.model.ResponseFormat
import src.charleex.vidgenius.whisper.model.transcription.Transcription
import src.charleex.vidgenius.whisper.model.transcription.TranscriptionRequest

interface TranscriptionService {
    /**
     * Formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm
     * Example: "$resourcesPrefix/multilingual.wav"
     *
     * @param filePath the path to the audio file. Formats: mp3, mp4, mpeg, mpga, m4a, wav, or webm.
     * Example: "$resourcesPrefix/multilingual.wav"
     */
    suspend fun transcriptAudio(
        filePath: String,
        fileSystem: FileSystem = FileSystem.SYSTEM,
        prompt: String? = null,
        language: String? = null
    ): Transcription
}

internal class TranscriptionServiceImpl(
    private val audioService: AudioService,
    private val model: ModelId,
) : TranscriptionService {
    override suspend fun transcriptAudio(
        filePath: String,
        fileSystem: FileSystem,
        prompt: String?,
        language: String?
    ): Transcription {
        val transcriptionRequest = TranscriptionRequest(
            audio = FileSource(filePath.toPath(), fileSystem),
            model = model,
            prompt = prompt,
            responseFormat = ResponseFormat.VERBOSE_JSON?.name?.lowercase(),
            language = language,
        )
        return audioService.transcription(transcriptionRequest)
    }
}


