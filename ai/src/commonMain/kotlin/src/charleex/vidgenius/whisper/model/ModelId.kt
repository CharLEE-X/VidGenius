package src.charleex.vidgenius.whisper.model

import kotlinx.serialization.Serializable

// Internal for now, but can be public if we have more models
@Serializable
internal class ModelId(val id: String)
