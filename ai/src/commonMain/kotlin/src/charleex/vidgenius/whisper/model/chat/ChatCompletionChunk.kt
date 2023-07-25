package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionChunk(
    @SerialName("id") val id: String,
    @SerialName("created") val created: Int,
    @SerialName("choices") val choices: List<ChatChunk>,
    @SerialName("usage") val usage: Usage? = null,
)
