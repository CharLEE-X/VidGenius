package com.charleex.vidgenius.datasource.feature.video_file.model

import java.io.File


enum class VideoType {
    MP4,
    MKV,
    AVI,
    UNKNOWN;
}

internal fun File.videoType(): VideoType {
    return when (extension) {
        "mp4" -> VideoType.MP4
        "mkv" -> VideoType.MKV
        "avi" -> VideoType.AVI
        else -> VideoType.UNKNOWN
    }
}
