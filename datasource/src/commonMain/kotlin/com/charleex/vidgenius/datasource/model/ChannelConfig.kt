package com.charleex.vidgenius.datasource.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ChannelConfig(
    open val id: String,
    open val title: String,
    open val secretsFile: String,
    open val category: String,
) {
    data class Animals(
        override val id: String = "UCmWNmg5PyF1VKUCttVcVzuw",
        override val title: String = "Roaring Laughter",
        override val secretsFile: String = "/youtube-animals.json",
        override val category: String = "animals",
    ) : ChannelConfig(id, title, secretsFile, category) {
        override fun toString(): String {
            return title
        }
    }

    data class Fails(
        override val id: String = "UCQq2M775lmNxxp_4rnlI4Vg",
        override val title: String = "Laugh Factory Fails",
        override val secretsFile: String = "/youtube-fails.json",
        override val category: String = "fails",
    ) : ChannelConfig(id, title, secretsFile, category) {
        override fun toString(): String {
            return title
        }
    }
}

val allChannels get() = listOf(ChannelConfig.Animals(), ChannelConfig.Fails())
