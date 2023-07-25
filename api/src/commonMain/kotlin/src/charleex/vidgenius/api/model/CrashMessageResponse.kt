package src.charleex.vidgenius.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CrashMessageResponse(
    @SerialName("message") val message: String
)

/**
 * Example response:
 * {
 *    "text": "Hello, how are you?"
 *  }
 */
