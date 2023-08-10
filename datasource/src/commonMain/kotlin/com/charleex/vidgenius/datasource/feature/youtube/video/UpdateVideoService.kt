package com.charleex.vidgenius.datasource.feature.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.datasource.feature.open_ai.model.ContentInfo
import com.charleex.vidgenius.datasource.feature.youtube.auth.GoogleAuth
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoLocalization
import com.google.common.collect.Lists
import java.io.IOException

internal interface UpdateVideoService {
    fun update(
        ytConfig: YtConfig,
        ytId: String,
        contentInfo: ContentInfo,
    ): Video?
}

internal class UpdateVideoServiceImpl(
    private val logger: Logger,
    private val googleAuth: GoogleAuth,
    private var httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
) : UpdateVideoService {
    companion object {
        const val QUOTA_COST = 50
        private const val APP_NAME = "youtube-cmdline-updatevideo-sample"
    }

    private val scopes: ArrayList<String> =
        Lists.newArrayList("https://www.googleapis.com/auth/youtube")
    private var youtube: YouTube? = null

    override fun update(
        ytConfig: YtConfig,
        ytId: String,
        contentInfo: ContentInfo,
    ): Video? {
        logger.d { "Updating video $ytId\nContentInfo: $contentInfo" }
        return try {
            val credential = googleAuth.authorize(scopes, ytConfig)

            youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME)
                .build()

            val listResponse = youtube!!.videos()
                .list(listOf("snippet", "status", "localizations"))
                .setId(listOf(ytId))
                .execute()

            val videoList = listResponse.items
                .ifEmpty { error("Can't find a video with ID: $ytId") }

            val video = videoList[0]

            val enUsLocalization = VideoLocalization().apply {
                title = contentInfo.enUS.title
                description = contentInfo.enUS.description
            }
            val esLocalization = VideoLocalization().apply {
                title = contentInfo.es.title
                description = contentInfo.es.description
            }
            val frLocalization = VideoLocalization().apply {
                title = contentInfo.zh.title
                description = contentInfo.zh.description
            }
            val ptLocalization = VideoLocalization().apply {
                title = contentInfo.pt.title
                description = contentInfo.pt.description
            }
            val hiLocalization = VideoLocalization().apply {
                title = contentInfo.hi.title
                description = contentInfo.hi.description
            }

            val multipleLocalizations = mutableMapOf<String, VideoLocalization>().apply {
                put("en-US", enUsLocalization)
                put("es", esLocalization)
//                put("fr", frLocalization)
//                put("pt", ptLocalization)
                put("hi", hiLocalization)
            }

            val snippet = video.snippet
            snippet.title = contentInfo.enUS.title
            snippet.description = contentInfo.enUS.description
            snippet.tags = contentInfo.tags
            snippet.defaultLanguage = "en-US"

            video.snippet = snippet
            video.localizations = multipleLocalizations
            video.status.privacyStatus = PrivacyStatus.PUBLIC.value

            val videoResponse = youtube!!.videos()
                .update(listOf("snippet", "status", "localizations"), video)
                .execute()
                ?: error("Can't update video with ID: $ytId")

            println("\n================== Returned Video ==================\n")
            println("  - ID: " + videoResponse.id)
            println("  - Title: " + videoResponse.snippet.title)
            println("  - Description: " + videoResponse.snippet.description)
            println("  - Tags: " + videoResponse.snippet.tags)
            println("  - PrivacyStatus: " + videoResponse.status.privacyStatus)
            println("  - Localizations: " + videoResponse.localizations)
            videoResponse.localizations.forEach {
                println("  - ${it.key} ${it.value}")
            }

            videoResponse
        } catch (e: GoogleJsonResponseException) {
            System.err.println(
                "GoogleJsonResponseException code: " + e.details.code + " : "
                        + e.details.message
            )
            e.printStackTrace()
            null
        } catch (e: IOException) {
            System.err.println("IOException: " + e.message)
            e.printStackTrace()
            null
        } catch (t: Throwable) {
            System.err.println("Throwable: " + t.message)
            t.printStackTrace()
            null
        }
    }
}
