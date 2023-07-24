package src.charleex.autoytvid.processor

import co.touchlab.kermit.Logger
import src.charleex.autoytvid.processor.model.Video
import src.charleex.autoytvid.processor.model.toVideo
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

interface FileProcessor {
    fun processFileSystemItems(items: List<*>): List<Video>
}

internal class FileProcessorImpl(
    private val logger: Logger,
) : FileProcessor {
    override fun processFileSystemItems(items: List<*>): List<Video> {
        val videos = mutableListOf<Video>()
        items.forEach { item ->
            try {
                val file = item as File
                when {
                    file.isFile -> {
                        handleFile(file)?.let {
                            videos.add(it.toVideo())
                        }
                    }

                    file.isDirectory -> {
                        videos += handleDirectory(file)
                    }

                    else -> {
                        logger.d("item is not type file or directory.")
                    }
                }
            } catch (e: Exception) {
                logger.d("Error while processing file system item: ${e.message}")
            }
        }
        return videos
    }

    private fun handleDirectory(file: File): List<Video> {
        val filesInDirectory = file.listFiles()
        return if (filesInDirectory != null) {
            processFileSystemItems(filesInDirectory.toList())
        } else {
            logger.d("files in directory is null.")
            emptyList()
        }
    }

    private fun handleFile(file: File): File? {
        val readable = if (!file.canRead()) {
            file.setReadable(true)
        } else false
        val video = isVideoFile(file.absolutePath)
        return if (readable && video) file else null
    }

    private fun isVideoFile(filePath: String): Boolean {
        val contentType = Files.probeContentType(Paths.get(filePath))
        return contentType?.startsWith("video/") ?: false
    }
}

