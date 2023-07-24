package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Logprobs(
    @SerialName("tokens") val tokens: List<String>,
    @SerialName("token_logprobs") val tokenLogprobs: List<Double>,
    @SerialName("top_logprobs") val topLogprobs: List<Map<String, Double>>,
    @SerialName("text_offset") val textOffset: List<Int>,
)
