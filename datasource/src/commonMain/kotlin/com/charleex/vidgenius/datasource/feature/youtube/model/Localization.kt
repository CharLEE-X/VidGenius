package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Localization(
    val languageCode: String,
    val title: String,
    val description: String,
)
