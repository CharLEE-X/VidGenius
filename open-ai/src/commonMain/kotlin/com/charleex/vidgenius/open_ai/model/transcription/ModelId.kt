package com.charleex.vidgenius.open_ai.model.transcription

import kotlinx.serialization.Serializable

// Internal for now, but can be public if we have more models
@Serializable
internal class ModelId(val id: String)
