package com.charleex.vidgenius.datasource.feature.video_file.model

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.ENTRY_CREATE
import java.nio.file.StandardWatchEventKinds.ENTRY_DELETE
import java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY
import java.nio.file.WatchKey

interface FileProcessor {
    val isWatching: StateFlow<Boolean>
    fun filterVideoFiles(items: List<*>): List<File>
    fun deleteFile(path: String)
    fun watchDirectory(directoryPath: String)
}

internal class FileProcessorImpl(
    private val logger: Logger,
) : FileProcessor {
    private val _isWatching = MutableStateFlow(false)
    override val isWatching: StateFlow<Boolean> = _isWatching.asStateFlow()

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

    override fun watchDirectory(directoryPath: String) {
        logger.d("Watching directory: ${directoryPath}")
        try {
            if (directoryPath.isEmpty()) {
                logger.d("Directory path is empty.")
                return
            }
            val path = Paths.get(directoryPath)
            val watchService = FileSystems.getDefault().newWatchService()

            // Register the directory with the watch service for specific events
            path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)

            // Start an infinite loop to listen for events
            while (true) {
                val key: WatchKey = try {
                    watchService.take()
                } catch (ex: InterruptedException) {
                    continue
                }

                for (event in key.pollEvents()) {
                    val kind = event.kind()

                    // Handle different kinds of events (e.g., file creation, deletion, modification)
                    when (kind) {
                        ENTRY_CREATE -> println("File created: ${event.context()}")
                        ENTRY_DELETE -> println("File deleted: ${event.context()}")
                        ENTRY_MODIFY -> println("File modified: ${event.context()}")
                    }
                }

                // Reset the key to receive further events
                key.reset()
            }
        } catch (e: Exception) {
            logger.d("Error watching directory: ${e.message}")
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

