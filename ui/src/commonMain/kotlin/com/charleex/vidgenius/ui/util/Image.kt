package com.charleex.vidgenius.ui.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.imageio.ImageIO

suspend fun imageFromFile(file: File, onError: (filePath: String) -> Unit): ImageBitmap? {
    val image = withContext(Dispatchers.IO) {
        try {
            ImageIO.read(file)
        } catch (e: Exception) {
            onError(file.absolutePath)
            null
        }
    }
    return image?.toComposeImageBitmap()
}
