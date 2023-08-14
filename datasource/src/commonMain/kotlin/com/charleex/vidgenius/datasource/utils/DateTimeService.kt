package com.charleex.vidgenius.datasource.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface DateTimeService {
    fun now(): Long
    fun nowInstant(): Instant
}

internal class DateTimeServiceImpl : DateTimeService {
    override fun nowInstant(): Instant = Clock.System.now()
    override fun now(): Long = nowInstant().toEpochMilliseconds()
}
