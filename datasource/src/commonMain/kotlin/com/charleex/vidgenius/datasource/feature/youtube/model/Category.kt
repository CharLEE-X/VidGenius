package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val title: String,
    val query: String,
    val channelLink: String,
)

val allCategories = listOf(
    Category(
        id = "1",
        title = "Animals",
        query = "funny animals",
        channelLink = "https://www.youtube.com/@RoaringLaughter-FunnyAnimals",
    ),
    Category(
        id = "2",
        title = "Fails",
        query = "epic fails",
        channelLink = "https://www.youtube.com/channel/UCQq2M775lmNxxp_4rnlI4Vg",
    ),
    Category(
        id = "3",
        title = "Hacks",
        query = "life hacks",
        channelLink = "https://www.youtube.com/channel/UCwcwNTCES6FcLAln8x8LxPw",
    ),
)
