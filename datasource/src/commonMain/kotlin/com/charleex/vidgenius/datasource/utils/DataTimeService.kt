package com.charleex.vidgenius.datasource.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface DataTimeService {
    fun now(): Long
    fun nowInstant(): Instant
}

internal class DateTimeServiceImpl : DataTimeService {
    override fun nowInstant(): Instant = Clock.System.now()
    override fun now(): Long = nowInstant().toEpochMilliseconds()
}
