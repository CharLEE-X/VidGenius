package src.charleex.vidgenius.processor.screenshot

import co.touchlab.kermit.Logger
import net.bramp.ffmpeg.FFprobe
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Java2DFrameConverter
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO


interface VideoScreenshotCapturing {
    fun captureScreenshots(
        file: File,
        percentages: List<Double>
    ): List<File>

    fun getVideoDuration(file: File): Double?
}

internal class VideoScreenshotCapturingImpl(
    private val logger: Logger,
    private val appDataDir: File,
) : VideoScreenshotCapturing {
    private val outputFolder: File by lazy {
        appDataDir.resolve("screenshots")
    }

    init {
        createOutputFolder(outputFolder)
    }

    override fun captureScreenshots(file: File, percentages: List<Double>): List<File> {
        logger.d { "Capturing screenshots" }
        return percentages.mapIndexed { index, percentage ->
            captureScreenshot(file, percentage, index)
        }
    }

    override fun getVideoDuration(file: File): Double {
        logger.d { "Getting video duration" }
        return try {
            val probeResult = FFprobe().probe(file.absolutePath)
            val format = probeResult.getFormat()
            format.duration
        } catch (e: IOException) {
            logger.e(e) { "Error getting video duration" }
            error("Error getting video duration")
        }
    }

    private fun captureScreenshot(inputFile: File, timestamp: Double, index: Int): File {
        logger.d { "Capturing screenshot $index" }
        val fileName = inputFile.nameWithoutExtension
        val outputFile = File(outputFolder, "${fileName}_${index + 1}.png")

        try {
            val frameGrabber = FFmpegFrameGrabber(inputFile)
            frameGrabber.format = "mp4"
            frameGrabber.start()

            val width = frameGrabber.imageWidth
            val height = frameGrabber.imageHeight
            println("width: $width, height: $height")

            val length = frameGrabber.lengthInTime
            logger.d { "Video length $length" }
            val time = (length * timestamp).toLong()
            logger.d { "Video time $time/$length ratio ${length / time}" }
            frameGrabber.timestamp = time

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
