package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FunctionCall(
    @SerialName("name") val name: String? = null,
    @SerialName("arguments") val arguments: String? = null,
)
