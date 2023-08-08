package com.charleex.vidgenius.ui.features.process.section.yt_video

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.db.YtVideo
import com.charleex.vidgenius.ui.components.AppCard
import com.charleex.vidgenius.ui.components.AppFlexSpacer
import com.charleex.vidgenius.ui.components.AppOutlinedButton
import com.charleex.vidgenius.ui.components.LocalImage
import java.util.Locale

@Composable
internal fun YtVideoItem(
    modifier: Modifier = Modifier,
    ytVideo: YtVideo,
    isFoundLocally: Boolean,
    onAddMultiLanguage: (YtVideo) -> Unit,
) {
    AppCard(
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Surface(
                color = MaterialTheme.colors.surface.copy(alpha = 0.2f),
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .padding(
                            start = 48.dp,
                            end = 64.dp,
                        )
                ) {
                    Text(
                        text = "${ytVideo.title} | ${ytVideo.id}",
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                    )
                    AppFlexSpacer()
                    AnimatedVisibility (!ytVideo.hasMultiLanguage) {
                        AppOutlinedButton(
                            label = "Add multi language",
                            icon = Icons.Default.Close,
                            iconTint = Color.Red,
                            enabled = true,
                            onClick = { onAddMultiLanguage(ytVideo) },
                            modifier = Modifier
                        )
                    }
                    AnimatedVisibility (ytVideo.hasMultiLanguage) {
                        AppOutlinedButton(
                            label = "Language OK",
                            icon = Icons.Default.Check,
                            iconTint = Color.Green,
                            enabled = true,
                            onClick = {},
                            modifier = Modifier
                        )
                    }
                    AnimatedVisibility (isFoundLocally) {
                        AppOutlinedButton(
                            label = "Found locally",
                            icon = Icons.Default.Check,
                            iconTint = Color.Green,
                            enabled = true,
                            onClick = {},
                            modifier = Modifier
                        )
                    }
                    AnimatedVisibility (!isFoundLocally) {
                        AppOutlinedButton(
                            label = "Not found locally",
                            icon = Icons.Default.Close,
                            iconTint = Color.Red,
                            enabled = true,
                            onClick = {},
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}
