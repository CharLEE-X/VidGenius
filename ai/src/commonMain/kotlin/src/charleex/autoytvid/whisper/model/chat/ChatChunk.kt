package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatChunk(
    @SerialName("index") val index: Int? = null,
    @SerialName("delta") val delta: ChatDelta? = null,
    @SerialName("finish_reason") val finishReason: String? = null,
)
