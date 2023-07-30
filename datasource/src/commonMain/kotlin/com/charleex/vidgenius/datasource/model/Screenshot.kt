package com.charleex.vidgenius.datasource.model

import com.benasher44.uuid.uuid4
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Screenshot(
    val id: String = uuid4().toString(),
    val path: String,
)
