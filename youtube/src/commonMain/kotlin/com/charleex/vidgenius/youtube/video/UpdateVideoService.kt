package com.charleex.vidgenius.youtube.video

import co.touchlab.kermit.Logger
import com.charleex.vidgenius.youtube.auth.GoogleAuth
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.services.youtube.YouTube
import com.google.common.collect.Lists
import java.io.IOException

internal interface UpdateVideoService {
    fun update(
        videoId: String,
        title: String?,
        description: String?,
        tags: List<String>?,
    )
}

internal class UpdateVideoServiceImpl(
    private val logger: Logger,
    private val googleAuth: GoogleAuth,
    private var httpTransport: HttpTransport,
    private val jsonFactory: JsonFactory,
) : UpdateVideoService {
    companion object {
        const val QUOTA_COST = 50
        private const val STORE = "updatevideo"
        private const val APP_NAME = "youtube-cmdline-updatevideo-sample"
    }

    private val scopes: ArrayList<String> = Lists.newArrayList("https://www.googleapis.com/auth/youtube")

    private var youtube: YouTube? = null

    override fun update(
        videoId: String,
        title: String?,
        description: String?,
        tags: List<String>?,
    ) {
        logger.d { "Updating video" }
        try {
            val credential = googleAuth.authorize(scopes, STORE)

            youtube = YouTube.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName(APP_NAME)
                .build()

            val listResponse = youtube!!.videos()
                .list(listOf("snippet"))
                .setId(listOf(videoId))
                .execute()

            // Since the API request specified a unique video ID, the API
            // response should return exactly one video. If the response does
            // not contain a video, then the specified video ID was not found.
            val videoList = listResponse.items
                .ifEmpty { error("Can't find a video with ID: $videoId") }

            val video = videoList[0]
            val snippet = video.snippet

            snippet.title = title
            snippet.description = description
            snippet.tags = tags

            video.snippet = snippet

            val videoResponse = youtube!!.videos()
                .update(listOf("snippet"), video)
                .execute()

            println("\n================== Returned Video ==================\n")
            println("  - ID: " + videoResponse.id)
            println("  - Title: " + videoResponse.snippet.title)
            println("  - Description: " + videoResponse.snippet.description)
            println("  - Tags: " + videoResponse.snippet.tags)

        } catch (e: GoogleJsonResponseException) {
            System.err.println(
                "GoogleJsonResponseException code: " + e.details.code + " : "
                        + e.details.message
            )
            e.printStackTrace()
        } catch (e: IOException) {
            System.err.println("IOException: " + e.message)
            e.printStackTrace()
        } catch (t: Throwable) {
            System.err.println("Throwable: " + t.message)
            t.printStackTrace()
        }
    }
}
