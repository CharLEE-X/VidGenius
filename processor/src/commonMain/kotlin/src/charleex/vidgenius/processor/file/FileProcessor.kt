package src.charleex.vidgenius.processor.file

import co.touchlab.kermit.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

interface FileProcessor {
    fun processFileSystemItems(items: List<*>): List<VideoFile>
}

internal class FileProcessorImpl(
    private val logger: Logger,
) : FileProcessor {
    override fun processFileSystemItems(items: List<*>): List<VideoFile> {
        logger.d("Processing file system items")
        val videoFiles = mutableListOf<VideoFile>()
        items.forEach { item ->
            try {
                val file = item as File
                when {
                    file.isFile -> {
                        handleFile(file)?.let {
                            videoFiles.add(it.toVideo())
                        }
                    }

                    file.isDirectory -> {
                        videoFiles += handleDirectory(file)
                    }

                    else -> {
                        logger.d("item is not type file or directory.")
                    }
                }
            } catch (e: Exception) {
                logger.d("Error while processing file system item: ${e.message}")
            }
        }
        logger.d("Processed ${videoFiles.size} video files")
        return videoFiles
    }

    private fun handleDirectory(file: File): List<VideoFile> {
        val filesInDirectory = file.listFiles()
        return if (filesInDirectory != null) {
            processFileSystemItems(filesInDirectory.toList())
        } else {
            logger.d("files in directory is null.")
            emptyList()
        }
    }

    private fun handleFile(file: File): File? {
        val contentType = Files.probeContentType(Paths.get(file.absolutePath))
        val isVideoFile = contentType?.startsWith("video/") ?: false
        return if (isVideoFile) {
            file
        } else {
            logger.d("File is not a video file.")
            null
        }
    }
}

