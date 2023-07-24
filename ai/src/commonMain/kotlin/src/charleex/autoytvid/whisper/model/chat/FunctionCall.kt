package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FunctionCall(
    @SerialName("name") val name: String? = null,
    @SerialName("arguments") val arguments: String? = null,
)
