package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatChoice(
    @SerialName("index") val index: Int? = null,
    @SerialName("message") val message: ChatMessage? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)
