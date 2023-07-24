package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatDelta(
    @SerialName("role") val role: ChatRole? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("function_call") val functionCall: FunctionCall? = null,
)
