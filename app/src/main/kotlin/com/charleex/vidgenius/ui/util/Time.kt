package com.charleex.vidgenius.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.pretty(): String {
    val localDateTime = toLocalDateTime(TimeZone.currentSystemDefault())
    val date = localDateTime.date
    val day = date.dayOfMonth
    val month = date.monthNumber
    val year = date.year
    val hour = localDateTime.time.hour
    val minute = localDateTime.time.minute
    return "$day/$month/$year $hour:$minute"
}
