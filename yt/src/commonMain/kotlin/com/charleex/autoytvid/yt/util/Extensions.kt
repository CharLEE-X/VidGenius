package com.charleex.autoytvid.yt.util

import com.google.api.client.util.DateTime
import kotlinx.datetime.Instant

fun DateTime.toInstant(): Instant {
    return Instant.fromEpochMilliseconds(value)
}
