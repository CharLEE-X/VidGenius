package com.charleex.vidgenius.datasource.feature.video_file.model

import co.touchlab.kermit.Logger
import java.io.File

interface FileProcessor {
    fun filterVideoFiles(items: List<*>): List<File>
    fun deleteFile(path: String)
}

internal class FileProcessorImpl(
    private val logger: Logger,
) : FileProcessor {
    override fun filterVideoFiles(items: List<*>): List<File> {
        logger.d("Processing file system items ${items.size}")
        val videoFiles = mutableListOf<File>()
        items.forEach { item ->
            try {
                val file = item as File
//                if (file.isFile) {
                    val newFile = handleFile(file)
                    if (newFile != null) {
                        videoFiles.add(newFile)
                    } else {
                        logger.d("File is not a video file.")
                    }
//                } else if (file.isDirectory) {
//                    videoFiles += handleDirectory(file)
//                } else {
//                    logger.v { "item is not type file or directory." }
//                }
            } catch (e: Exception) {
                logger.d("Error while processing file system item: ${e.message}")
            }
        }
        logger.d("Processed ${videoFiles.size} video files")
        return videoFiles
    }

    override fun deleteFile(path: String) {
        logger.d("Deleting file: ${path}")
        try {
            val file = File(path)
            file.delete()
        } catch (e: Exception) {
            logger.d("Error deleting file: ${e.message}")
        }
    }

    private fun handleDirectory(file: File): List<File> {
        val filesInDirectory = file.listFiles()
        return if (filesInDirectory != null) {
            logger.d("files in directory is not null.")
            filterVideoFiles(filesInDirectory.toList())
        } else {
            logger.d("files in directory is null.")
            emptyList()
        }
    }

    private fun handleFile(file: File): File? {
//        val contentType = Files.probeContentType(Paths.get(file.absolutePath))
//        val isVideoFile = contentType?.startsWith("video/") ?: false
        return if (true) {
            logger.d("File is a video file.")
            file
        } else {
            logger.d("File is not a video file.")
            null
        }
    }
}

