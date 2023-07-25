package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.util.imageFromBufferedImage
import java.io.File

@Composable
fun ImageFromBufferedImage(
    modifier: Modifier = Modifier,
    file: File,
    contentScale: ContentScale = ContentScale.Crop,
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(file) {
        bitmap = imageFromBufferedImage(file)
    }

    bitmap?.let { loadedBitmap ->
        Image(
            bitmap = loadedBitmap,
            contentDescription = "Loaded Screenshot",
            contentScale = contentScale,
            modifier = modifier
        )
    } ?: Surface(
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.Gray,
        ),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.BrokenImage,
            contentDescription = "Drag and drop",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
        )
    }
}
