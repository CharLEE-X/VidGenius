package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class ChannelConfig(
    val id: String,
    val title: String,
    val secretsFile: String,
    val category: String,
)

val ytChannels = listOf(
    ChannelConfig(
        id = "config_1",
        title = "Roaring Laughter 1",
        secretsFile = "/vid_genius_4.json",
        category = "funny animals",
    ),
    ChannelConfig(
        id = "config_2",
        title = "Roaring Laughter 2",
    secretsFile = "/vid_genius3.json",
        category = "funny animals",
    ),
    ChannelConfig(
        id = "config_3",
        title = "Roaring Laughter 3",
    secretsFile = "/youtube-animals.json",
        category = "funny animals",
    ),
    ChannelConfig(
        id = "config_4",
        title = "Laugh Factory Fails 1",
    secretsFile = "/aw_portfolio.json",
        category = "funny animals",
    ),
    ChannelConfig(
        id = "config_5",
        title = "Laugh Factory Fails 2",
        secretsFile = "/youtube-fails.json",
        category = "epic fails",
    ),
)
