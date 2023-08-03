package com.charleex.vidgenius.datasource.model

import kotlinx.datetime.Instant

data class YtUploadItem(
    val id: String,
    val title: String,
    val description: String? = null,
    val publishedAt: Instant,
)
