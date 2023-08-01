package com.charleex.vidgenius.yt.mock

import com.charleex.vidgenius.youtube.video.ChannelUploadsService
import com.charleex.vidgenius.youtube.model.ChannelUploadsItem
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant

internal val channelUploadServiceMock = object : ChannelUploadsService {
    override suspend fun getUploadList(): List<ChannelUploadsItem> {
        delay(500)
        return mockUploads
    }

    override suspend fun getVideoDetail(videoId: String): ChannelUploadsItem {
        delay(500)
        return mockUploads.first()
    }
}

internal val mockUploads = (1..10).map {
    ChannelUploadsItem(
        videoId = "3WG0uIgG-H0",
        title = "\uD83D\uDE3B\uD83D\uDC3E Aww-inspiring Cuteness: Adorable Moments with a Cute Cat!\uD83D\uDE3B\uD83D\uDC3E",
        description = """
            Description:
            Prepare to be overwhelmed by cuteness as we dive into the world of an adorable cat and witness heart-melting moments that will make you go "aww"! 😻🥰🐾 Watch with pure delight as this furry friend captivates us with its irresistible charm, endearing gestures, and precious meows. From its playful antics and graceful stretches to its cozy snuggles and curious explorations, this cute cat will surely melt your heart. Join us on this journey filled with warmth and love as we celebrate the beauty and companionship that cats bring into our lives. Let's appreciate their gentle purrs, their captivating eyes, and the joy they bring with their presence. Get ready to swoon and feel the overwhelming cuteness as we witness the adorable moments of this cute cat! 🌟😍🐾

            Tags:

            #CuteCat
            #AdorableMoments
            #AwwInspiringCuteness
            #IrresistibleCharm
            #EndearingGestures
            #PreciousMeows
            #PlayfulAntics
            #GracefulStretches
            #CozySnuggles
            #CuriousExplorations
            #HeartMelting
            #WarmthAndLove
            #GentlePurrs
            #CaptivatingEyes
            #OverwhelmingCuteness
        """.trimIndent(),
        publishedAt = Instant.parse("2023-07-09T10:36:45Z"),
    )
}
