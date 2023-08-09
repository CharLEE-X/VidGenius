package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class YtConfig(
    val id: String,
    val title: String,
    val secretsFile: String,
    val category: String,
)

val ytConfigs = listOf(
    YtConfig(
        id = "1",
        title = "One",
        secretsFile = "/vid_genius_4.json",
        category = "funny animals",
    ),
    YtConfig(
        id = "config_2",
        title = "Two",
    secretsFile = "/vid_genius3.json",
        category = "funny animals",
    ),
    YtConfig(
        id = "config_3",
        title = "Three",
    secretsFile = "/youtube-animals.json",
        category = "funny animals",
    ),
    YtConfig(
        id = "config_4",
        title = "Four",
    secretsFile = "/aw_portfolio.json",
        category = "epic fails",
    ),
    YtConfig(
        id = "config_5",
        title = "Five",
        secretsFile = "/youtube-fails.json",
        category = "epic fails",
    ),
)
