package com.charleex.vidgenius.ui.features.video_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.components.SegmentDefaults
import com.charleex.vidgenius.ui.components.SegmentSpec
import com.charleex.vidgenius.ui.components.SegmentsGroup
import com.charleex.vidgenius.ui.util.pretty
import com.lt.load_the_image.rememberImagePainter

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun VideoDetailContent(
    video: Video,
    scroll: (LazyListState) -> Unit,
) {
    val layColumnState = rememberLazyListState()
    val title = video.ytVideo?.title ?: video.localVideo?.name ?: "Unknown title"

    LaunchedEffect(layColumnState) {
        scroll(layColumnState)
    }

    Surface(
        shape = CutCornerShape(32.dp),
        tonalElevation = 4.dp,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                PaddingValues(
                    top = 120.dp,
                    start = 32.dp,
                    end = 32.dp,
                    bottom = 32.dp
                )
            )
    ) {
        LazyColumn(
            state = layColumnState,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(
                top = 32.dp,
                start = 32.dp,
                end = 32.dp,
                bottom = 32.dp
            ),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 32.dp)
                    ) {
                        video.ytVideo?.let { ytVideo ->
                            val privacyWithIcons = mapOf(
                                PrivacyStatus.PUBLIC.value to Icons.Default.Visibility,
                                PrivacyStatus.PRIVATE.value to Icons.Default.VisibilityOff,
                                PrivacyStatus.UNLISTED.value to Icons.Default.Pending,
                            )

                            var newTitle by remember { mutableStateOf(ytVideo.title) }
                            var newDescription by remember { mutableStateOf(ytVideo.description) }
                            var newTags by remember { mutableStateOf(ytVideo.tags) }
                            var newPrivacyIndex by remember { mutableStateOf(ytVideo.privacyStatus.ordinal) }

                            LaunchedEffect(ytVideo) {
                                newTitle = ytVideo.title
                                newDescription = ytVideo.description
                                newTags = ytVideo.tags
                                newPrivacyIndex = ytVideo.privacyStatus.ordinal
                            }

                            val categorySegments = privacyWithIcons.map { (name, icon) ->
                                SegmentSpec(
                                    icon = icon,
                                    label = name,
                                    colors = SegmentDefaults.defaultColors(),
                                    corners = SegmentDefaults.defaultCorners(),
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Published: ${ytVideo.publishedAt?.pretty()}",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                SegmentsGroup(
                                    segments = categorySegments,
                                    selectedIndexes = listOf(newPrivacyIndex),
                                    onSegmentClicked = { newPrivacyIndex = it },
                                    segmentModifier = Modifier
                                        .height(40.dp),
                                    modifier = Modifier
                                        .width(400.dp)
                                )
                            }

                            AppTextField(
                                label = "Title",
                                value = newTitle,
                                onValueChange = { newTitle = it },
                                supportingText = {
                                    AnimatedVisibility(visible = newTitle != ytVideo.title) {
                                        Text(text = "Changed")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            AppTextField(
                                label = "Description",
                                value = newDescription,
                                onValueChange = { newDescription = it },
                                supportingText = {
                                    AnimatedVisibility(visible = newDescription != ytVideo.description) {
                                        Text(text = "Changed")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Box(
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    newTags.forEach {
                                        Chip(
                                            shape = CutCornerShape(8.dp),
                                            onClick = {
                                                newTags = newTags.filter { tag -> tag != it }
                                            }
                                        ) {
                                            Text(text = it)
                                        }
                                    }

                                    ElevatedAssistChip(
                                        label = {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Add tag",
                                            )
                                        },
                                        shape = CutCornerShape(8.dp),
                                        onClick = {}
                                    )

                                    var newTag by remember { mutableStateOf("") }
                                    BasicTextField(
                                        value = newTag,
                                        onValueChange = { newTag = it },
                                    )
                                }
                            }
                        }

                        Text(text = "YouTube video id:: ${video.localVideo?.id}")
                        Text(text = "Name: ${video.localVideo?.name}")
                        Text(text = "Path: ${video.localVideo?.path}")
                        Row {
                            video.localVideo?.screenshots?.forEach {
                                LocalImage(
                                    filePath = it,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                ) {}
                            }
                        }
                        Text(text = "Path: ${video.localVideo?.screenshots}")
                        Text(text = "Path: ${video.localVideo?.descriptions}")
                        Text(text = "Path: ${video.localVideo?.descriptionContext}")
                        Text(text = "CreatedAt: ${video.localVideo?.createdAt?.pretty()}")
                        Column(
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            video.localVideo?.localizations?.forEach { (s, pair) ->
                                Text(text = "Path: $s")
                                Text(text = "Path: ${pair.first}")
                                Text(text = "Path: ${pair.second}")
                            }
                        }
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(240.dp)
                    ) {
                        video.ytVideo?.let { ytVideo ->
                            Image(
                                painter = rememberImagePainter(ytVideo.thumbnailLarge),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Generate")
                        }
                        FilledTonalButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Update")
                        }
                        FilledTonalButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun AppTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = !enabled,
    supportingText: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        shape = CutCornerShape(8.dp),
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        readOnly = readOnly,
        label = { Text(text = label) },
        supportingText = supportingText,
        modifier = modifier,
    )
}
