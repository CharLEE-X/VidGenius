package com.charleex.autoytvid.ui.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun String.toDateTimeFormatted(): String {
    val datetime = Instant.fromEpochMilliseconds(this.toLong())
        .toLocalDateTime(TimeZone.currentSystemDefault())
    return "${datetime.date} ${datetime.hour}:${datetime.minute}"
}

fun Long.toDate() = Instant.fromEpochMilliseconds(this)
    .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.displayNicely() = "$year-$monthNumber-$dayOfMonth $hour:$minute"
