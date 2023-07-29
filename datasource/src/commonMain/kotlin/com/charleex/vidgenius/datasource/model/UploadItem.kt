package com.charleex.vidgenius.datasource.model

import kotlinx.datetime.Instant

data class UploadItem(
    val id: String,
    val title: String,
    val description: String,
    val publishedAt: Instant,
)
