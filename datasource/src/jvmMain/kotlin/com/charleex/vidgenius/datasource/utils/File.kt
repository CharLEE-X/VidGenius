package com.charleex.vidgenius.datasource.utils

import java.io.File
import java.nio.file.Files

actual fun renameFile(directoryPath: String, oldName: String, newName: String): String {
    val oldFile = File(directoryPath, oldName)
    val newFile = File(directoryPath, newName)
    val newPath = Files.move(oldFile.toPath(), newFile.toPath())
    println("newPath: $newPath")
    return newPath.fileName.toString()
}
