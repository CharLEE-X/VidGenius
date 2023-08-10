package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class YtConfig(
    val id: String,
    val title: String,
    val secretsFile: String,
)

val ytConfigs = listOf(
    YtConfig(
        id = "1",
        title = "One",
        secretsFile = "/vid_genius_4.json",
    ),
    YtConfig(
        id = "2",
        title = "Two",
        secretsFile = "/vid_genius3.json",
    ),
    YtConfig(
        id = "3",
        title = "Three",
        secretsFile = "/youtube-animals.json",
    ),
    YtConfig(
        id = "4",
        title = "Four",
        secretsFile = "/aw_portfolio.json",
    ),
    YtConfig(
        id = "5",
        title = "Five",
        secretsFile = "/youtube-fails.json",
    ),
)
