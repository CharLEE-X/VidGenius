package src.charleex.vidgenius.repository.model

data class AudioTranscription(
    val id: Int,
    val originalText: String,
    val englishText: String,
    val language: String?,
    val transcriptionTime: Long,
    val translationTime: Long,
)
