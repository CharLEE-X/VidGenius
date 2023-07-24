package src.charleex.autoytvid.whisper.model.chat

import kotlinx.serialization.Serializable

@Serializable
class ChatRole(val role: String) {
    companion object {
        val System: ChatRole = ChatRole("system")
        val User: ChatRole = ChatRole("user")
        val Assistant: ChatRole = ChatRole("assistant")
        val Function: ChatRole = ChatRole("function")
    }
}
