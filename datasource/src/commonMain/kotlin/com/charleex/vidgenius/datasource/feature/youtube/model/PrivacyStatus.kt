package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable


@Serializable
enum class PrivacyStatus(val value: String) {
    PUBLIC("Public"),
    PRIVATE("Private"),
    UNLISTED("Unlisted")
    ;
}

fun privacyStatusFromString(value: String): PrivacyStatus {
    return when (value.lowercase()) {
        "public" -> PrivacyStatus.PUBLIC
        "private" -> PrivacyStatus.PRIVATE
        "unlisted" -> PrivacyStatus.UNLISTED
        else -> error("Unknown privacy status: $value")
    }
}
