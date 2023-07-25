package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    @SerialName("role") val role: ChatRole,
    @SerialName("content") val content: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("function_call") val functionCall: FunctionCall? = null,
)
