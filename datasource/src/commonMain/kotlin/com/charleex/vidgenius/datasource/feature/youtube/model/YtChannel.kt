package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class YtChannel(
    val id: String,
    val title: String,
    val secretsFile: String,
)

val ytChannels = listOf(
    YtChannel(
        id = "UCmWNmg5PyF1VKUCttVcVzuw",
        title = "Roaring Laughter",
        secretsFile = "youtube-roaring_laughter.json",
    ),
    YtChannel(
        id = "UCwcwNTCES6FcLAln8x8LxPw",
        title = "30 Seconds Hacks",
        secretsFile = "youtube-30_seconds_hacks.json",
    ),
)
