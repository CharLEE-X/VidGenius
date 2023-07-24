package com.charleex.autoytvid.ui.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideoLibrary
import com.charleex.autoytvid.feature.router.RouterScreen

internal fun RouterScreen?.icon() = when (this) {
    RouterScreen.FeatureList -> Icons.Filled.List
    RouterScreen.Login -> Icons.Filled.Login
    RouterScreen.DragDrop -> Icons.Filled.DragIndicator
    RouterScreen.VideoList -> Icons.Filled.List
    RouterScreen.VideoDetail -> Icons.Filled.VideoFile
    null -> Icons.Filled.Error
}
