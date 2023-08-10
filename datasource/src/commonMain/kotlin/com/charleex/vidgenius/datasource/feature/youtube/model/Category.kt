package com.charleex.vidgenius.datasource.feature.youtube.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val title: String,
    val query: String,
)

val allCategories = listOf(
    Category(
        id = "1",
        title = "Animals",
        query = "funny animals",
    ),
    Category(
        id = "2",
        title = "Fails",
        query = "epic fails",
    ),
    Category(
        id = "3",
        title = "Hacks",
        query = "life hacks",
    ),
)
