package com.charleex.vidgenius.ui.components.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.ui.features.generation.loadImage

@Composable
fun AppListItem(
    title: String,
    thumbnailUrl: String?,
    privacyStatus: PrivacyStatus,
    publishedAt: String,
) {
    val privacyIcon = when (privacyStatus) {
        PrivacyStatus.PUBLIC -> Icons.Default.Visibility
        PrivacyStatus.UNLISTED -> Icons.Default.Pending
        PrivacyStatus.PRIVATE -> Icons.Default.VisibilityOff
    }
    val tonalElevation = when (privacyStatus) {
        PrivacyStatus.PUBLIC -> 1.dp
        PrivacyStatus.UNLISTED -> 3.dp
        PrivacyStatus.PRIVATE -> 6.dp
    }

    Surface(
        tonalElevation = tonalElevation,
        shape = CutCornerShape(12.dp)
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(title)
                },
                leadingContent = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        thumbnailUrl?.let {
                            Image(
                                loadImage(it),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = privacyIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .width(16.dp)
                                .height(16.dp)
                        )
                        Text(publishedAt)
                    }
                },
                trailingContent = {
                    // TODO: Add more
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
