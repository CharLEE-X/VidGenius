package src.charleex.vidgenius.whisper.model.chat

import kotlinx.serialization.Serializable

/**
 * The role of the author of this message.
 */
@Serializable
class ChatRole(val role: String) {
    companion object {
        val System: ChatRole = ChatRole("system")
        val User: ChatRole = ChatRole("user")
        val Assistant: ChatRole = ChatRole("assistant")
        val Function: ChatRole = ChatRole("function")
    }
}
