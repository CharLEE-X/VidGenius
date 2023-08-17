package com.charleex.vidgenius.ui.components.list

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.ui.util.pretty
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppListItem(
    video: Video,
    videoService: VideoService,
    onClick: (String) -> Unit,
) {
    video.ytVideo?.let { ytVideo ->
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        var isHovered by remember { mutableStateOf(false) }

        val privacyIcon = when (ytVideo.privacyStatus) {
            PrivacyStatus.PUBLIC -> Icons.Default.Visibility
            PrivacyStatus.UNLISTED -> Icons.Default.Pending
            PrivacyStatus.PRIVATE -> Icons.Default.VisibilityOff
        }
        val tonalElevation = when (ytVideo.privacyStatus) {
            PrivacyStatus.PUBLIC -> 1.dp
            PrivacyStatus.UNLISTED -> 3.dp
            PrivacyStatus.PRIVATE -> 6.dp
        }

        val bgColor = when {
            isHovered && isPressed -> tonalElevation + 6.dp
            isHovered && !isPressed -> tonalElevation + 1.dp
            else -> tonalElevation
        }

        val scale by animateFloatAsState(
            targetValue = when {
                isHovered && isPressed -> 0.99f
                isHovered && !isPressed -> 1f
                else -> 1f
            }
        )

        Surface(
            tonalElevation = bgColor,
            shape = CutCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = when {
                    isHovered && isPressed -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    isHovered && !isPressed -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            ),
            modifier = Modifier
                .onPointerEvent(
                    eventType = PointerEventType.Enter,
                    onEvent = { isHovered = true },
                )
                .onPointerEvent(
                    eventType = PointerEventType.Exit,
                    onEvent = { isHovered = false },
                )
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Checkbox(
                    checked = video.id in videoService.selectedVideos.value.map { it.id },
                    onCheckedChange = {
                        videoService.onSelection(video)
                    },
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp)
                )
                ListItem(
                    headlineContent = {
                        Text(video.ytVideo?.title ?: video.localVideo?.name ?: "Unknown")
                    },
                    leadingContent = {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            val thumbnail =
                                video.ytVideo?.thumbnailSmall ?: video.ytVideo?.thumbnailLarge
                            thumbnail?.let {
                                Image(
                                    painter = rememberImagePainter(it),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(60.dp)
                                        .height(60.dp)
                                )
                            } ?: Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                            )
                        }
                    },
                    supportingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(
                                imageVector = privacyIcon,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(16.dp)
                            )
                            video.ytVideo?.publishedAt?.let {
                                Text(it.pretty())
                            }
                        }
                    },
                    trailingContent = {
                        // TODO: Add more
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = interactionSource,
                            onClick = { onClick(video.id) }
                        )
                )
            }
        }
    }
}
