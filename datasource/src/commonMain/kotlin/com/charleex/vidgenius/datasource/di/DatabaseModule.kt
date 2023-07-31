package com.charleex.vidgenius.datasource.di

import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.model.Screenshot
import com.hackathon.cda.repository.db.VidGeniusDatabase
import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.serializers.InstantComponentSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.dsl.module

internal val databaseModule = module {
    single {
        VidGeniusDatabase(
            driver = get(),
            VideoAdapter = get(),
        )
    }
    single {
        Video.Adapter(
            screenshotsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
            descriptionsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
            createdAtAdapter = InstantComponentSerializer.asColumnAdapter(),
            modifiedAtAdapter = InstantComponentSerializer.asColumnAdapter(),
            tagsAdapter = ListSerializer(String.serializer()).asColumnAdapter(),
        )
    }
}

private fun <T : Any> KSerializer<T>.asColumnAdapter(json: Json = Json { ignoreUnknownKeys = true }) =
    JsonColumnAdapter(json, this)

private class JsonColumnAdapter<T : Any>(private val json: Json, private val serializer: KSerializer<T>) :
    ColumnAdapter<T, String> {
    override fun decode(databaseValue: String): T = json.decodeFromString(serializer, databaseValue)
    override fun encode(value: T): String = json.encodeToString(serializer, value)
}
