package com.charleex.vidgenius.ui.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.imageio.ImageIO

suspend fun imageFromBufferedImage(file: File): ImageBitmap {
    val image = withContext(Dispatchers.IO) {
        ImageIO.read(file)
    }
    return image.toComposeImageBitmap()
}
