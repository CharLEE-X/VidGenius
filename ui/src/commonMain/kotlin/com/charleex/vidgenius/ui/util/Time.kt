package com.charleex.vidgenius.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun Instant.pretty(): String {
    val localDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
    val date = localDateTime.date
    val time = localDateTime.time
    return "$date $time"
}
