package com.charleex.vidgenius.datasource.youtube.youtube

import com.google.common.collect.Lists

object YoutubeConfig {
    object UploadList {
        const val NAME = "youtube-cmdline-myuploads-sample"
        const val STORE = "updatevideo"
        val scope = Scopes.readOnly
    }

    object UploadVideo {
        const val NAME = "youtube-cmdline-uploadvideo-sample"
        const val STORE = "uploadvideo"
        val scope = Scopes.upload
    }

    object UpdateVideo {
        const val NAME = "youtube-cmdline-myuploads-sample"
        const val STORE = "updatevideo"
        val scope = Scopes.readWrite
    }

    object YouTubeAnalyticsReports {
        const val NAME = "youtube-analytics-api-report-example"
        const val STORE = "analyticsreports"
        val scope = Scopes.readOnlyAnalytics
    }

    private object Scopes {
        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        val readOnly: ArrayList<String> = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly")

        // These scopes are required to access information about the
        // authenticated user's YouTube channel as well as Analytics
        // data for that channel.
        val readOnlyAnalytics: ArrayList<String> = Lists.newArrayList(
            "https://www.googleapis.com/auth/yt-analytics.readonly",
            "https://www.googleapis.com/auth/youtube.readonly"
        )

        // This OAuth 2.0 access scope allows an application to upload files
        // to the authenticated user's YouTube channel, but doesn't allow
        // other types of access.
        val upload: ArrayList<String> = Lists.newArrayList("https://www.googleapis.com/auth/youtube.upload")

        // This OAuth 2.0 access scope allows for full read/write access to the
        // authenticated user's account.
        val readWrite: ArrayList<String> = Lists.newArrayList("https://www.googleapis.com/auth/youtube")
    }
}
