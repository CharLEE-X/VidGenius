package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class ChannelConfig(
    val id: String,
    val title: String,
    val secretsFile: String,
    val category: String,
    val links: Map<String, String>
)

private val roaringLaughterChannelConfig = ChannelConfig(
    id = "UCmWNmg5PyF1VKUCttVcVzuw",
    title = "Roaring Laughter - Funny Animals",
    secretsFile = "/youtube-animals.json",
    category = "animals",
    links = mapOf(
        "YouTube:" to "@RoaringLaughter-FunnyAnimals",
        "TikTok:" to "@Roaring_Laughter",
    )
)

private val failsChannelConfig = ChannelConfig(
    id = "UCQq2M775lmNxxp_4rnlI4Vg",
    title = "Laugh Factory Fails: Guaranteed Smiles Every Time",
    secretsFile = "/youtube-fails.json",
    category = "fails",
    links = mapOf(
        "YouTube:" to "@Laugh-Factory-Fails",
    )
)

val ytChannels = listOf(roaringLaughterChannelConfig, failsChannelConfig)
