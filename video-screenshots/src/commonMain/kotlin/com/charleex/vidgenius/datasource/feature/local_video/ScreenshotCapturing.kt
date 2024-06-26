package com.charleex.vidgenius.datasource.feature.local_video

import co.touchlab.kermit.Logger
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

interface ScreenshotCapturing {
    fun captureScreenshot(
        file: File,
        timestamp: Long,
        index: Int,
    ): File

    fun getVideoDuration(file: File): Long
}

internal class ScreenshotCapturingImpl(
    private val logger: Logger,
    private val appDataDirFile: File,
) : ScreenshotCapturing {
    private val outputFolder: File by lazy {
        appDataDirFile.resolve("screenshots")
    }

    init {
        createOutputFolder(outputFolder)
    }

    override fun getVideoDuration(file: File): Long {
        logger.d { "Getting video duration" }
        return try {
            logger.d { "File path: ${file.path}" }
            val frameGrabber = FFmpegFrameGrabber(file)
            frameGrabber.start()
            frameGrabber.format = "mp4"
            val duration = frameGrabber.lengthInTime
            frameGrabber.stop()
            frameGrabber.release()
            duration
        } catch (e: IOException) {
            error("Error getting video duration")
        }
    }

    override fun captureScreenshot(file: File, timestamp: Long, index: Int): File {
        logger.d { "Capturing screenshot $index" }
        val fileName = file.nameWithoutExtension
        val outputFile = File(outputFolder, "${fileName}_${index + 1}.png")

        try {
            val frameGrabber = FFmpegFrameGrabber(file)
            frameGrabber.format = "mp4"
            frameGrabber.start()

            val width = frameGrabber.imageWidth
            val height = frameGrabber.imageHeight
            println("width: $width, height: $height")

            frameGrabber.timestamp = timestamp
            val duration = frameGrabber.lengthInTime
            logger.d { "Duration: $duration" }

            val frame = frameGrabber.grabImage() ?: throw java.lang.Exception("Frame is NULL!")
            if (frame.image == null) error("Frame Image is NULL!")

            val bufferedImage = Java2DFrameConverter().convert(frame)
            ImageIO.write(bufferedImage, "png", outputFile)

            frameGrabber.stop()
        } catch (e: java.lang.Exception) {
            throw java.lang.Exception("Error Getting Image", e)
        }

        return outputFile
    }

    private fun createOutputFolder(outputFolder: File) {
        logger.d { "Creating output folder" }
        outputFolder.mkdirs()
    }
}
