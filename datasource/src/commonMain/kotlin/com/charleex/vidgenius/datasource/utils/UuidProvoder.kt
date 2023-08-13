package com.charleex.vidgenius.datasource.utils

import com.benasher44.uuid.uuid4

interface UuidProvider {
    fun uuid(): String
}

internal class UuidProviderImpl : UuidProvider {
    override fun uuid(): String = uuid4().toString()
}
