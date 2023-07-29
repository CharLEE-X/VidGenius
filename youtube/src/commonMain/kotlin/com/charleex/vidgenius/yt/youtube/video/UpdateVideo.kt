package com.charleex.vidgenius.yt.youtube.video

import co.touchlab.kermit.Logger
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.services.youtube.YouTube
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.system.exitProcess

/**
 * Update a video by adding a keyword tag to its metadata. The demo uses the
 * YouTube Data API (v3) and OAuth 2.0 for authorization.
 */
interface UpdateVideo {
    /**
     * Add a keyword tag to a video that the user specifies. Use OAuth 2.0 to
     * authorize the API request.
     */
    fun update()
}

internal class UpdateVideoImpl(
    private val logger: Logger,
    private var youtube: YouTube
) : UpdateVideo {

    /*
     * Prompt the user to enter a keyword tag.
     */
    private// If the user doesn't enter a tag, use the default value "New Tag."
    val tagFromUser: String
        @Throws(IOException::class)
        get() {

            var keyword = ""

            print("Please enter a tag for your video: ")
            val bReader = BufferedReader(InputStreamReader(System.`in`))
            keyword = bReader.readLine()

            if (keyword.length < 1) {
                keyword = "New Tag"
            }
            return keyword
        }

    /*
     * Prompt the user to enter a video ID.
     */
    private// Exit if the user doesn't provide a value.
    val videoIdFromUser: String
        @Throws(IOException::class)
        get() {

            var videoId = ""

            print("Please enter a video Id to update: ")
            val bReader = BufferedReader(InputStreamReader(System.`in`))
            videoId = bReader.readLine()

            if (videoId.isEmpty()) {
                print("Video Id can't be empty!")
                exitProcess(1)
            }

            return videoId
        }

    override fun update() {
        logger.d { "Updating video" }

        try {
            // Prompt the user to enter the video ID of the video being updated.
            val videoId = videoIdFromUser
            println("You chose $videoId to update.")

            // Prompt the user to enter a keyword tag to add to the video.
            val tag = tagFromUser
            println("You chose $tag as a tag.")

            // Call the YouTube Data API's youtube.videos.list method to
            // retrieve the resource that represents the specified video.
            val listVideosRequest = youtube.videos().list("snippet").setId(videoId)
            val listResponse = listVideosRequest.execute()

            // Since the API request specified a unique video ID, the API
            // response should return exactly one video. If the response does
            // not contain a video, then the specified video ID was not found.
            val videoList = listResponse.items
            if (videoList.isEmpty()) {
                println("Can't find a video with ID: $videoId")
                return
            }

            // Extract the snippet from the video resource.
            val video = videoList[0]
            val snippet = video.snippet

            // Preserve any tags already associated with the video. If the
            // video does not have any tags, create a new array. Append the
            // provided tag to the list of tags associated with the video.
            var tags: MutableList<String>? = snippet.tags
            if (tags == null) {
                tags = ArrayList(1)
                snippet.tags = tags
            }
            tags.add(tag)

            // Update the video resource by calling the videos.update() method.
            val updateVideosRequest = youtube.videos().update("snippet", video)
            val videoResponse = updateVideosRequest.execute()

            // Print information from the updated resource.
            println("\n================== Returned Video ==================\n")
            println("  - Title: " + videoResponse.snippet.title)
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
