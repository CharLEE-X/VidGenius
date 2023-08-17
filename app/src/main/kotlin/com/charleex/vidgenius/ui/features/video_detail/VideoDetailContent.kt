package com.charleex.vidgenius.ui.features.video_detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.db.Video
import com.charleex.vidgenius.datasource.feature.youtube.model.PrivacyStatus
import com.charleex.vidgenius.ui.components.LocalImage
import com.charleex.vidgenius.ui.components.SegmentDefaults
import com.charleex.vidgenius.ui.components.SegmentSpec
import com.charleex.vidgenius.ui.components.SegmentsGroup
import com.charleex.vidgenius.ui.util.pretty
import com.lt.load_the_image.rememberImagePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun VideoDetailContent(
    video: Video,
    videoService: VideoService,
    scroll: (LazyListState) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val layColumnState = rememberLazyListState()

    var newTitle by remember { mutableStateOf(video.ytVideo?.title) }
    var newDescription by remember { mutableStateOf(video.ytVideo?.description) }
    var newTags by remember { mutableStateOf(video.ytVideo?.tags) }
    var newPrivacyIndex by remember { mutableStateOf(video.ytVideo?.privacyStatus?.ordinal) }

    var isTitleChanged by remember { mutableStateOf(false) }
    var isDescriptionChanged by remember { mutableStateOf(false) }
    var isTagsChanged by remember { mutableStateOf(false) }
    var isPrivacyChanged by remember { mutableStateOf(false) }

    LaunchedEffect(video.ytVideo) {
        newTitle = video.ytVideo?.title
        newDescription = video.ytVideo?.description
        newTags = video.ytVideo?.tags
        newPrivacyIndex = video.ytVideo?.privacyStatus?.ordinal
    }

    LaunchedEffect(newTitle) {
        isTitleChanged = newTitle != video.ytVideo?.title
    }

    LaunchedEffect(newDescription) {
        isDescriptionChanged = newDescription != video.ytVideo?.description
    }

    LaunchedEffect(newTags) {
        isTagsChanged = newTags != video.ytVideo?.tags
    }

    LaunchedEffect(newPrivacyIndex) {
        isPrivacyChanged = newPrivacyIndex != video.ytVideo?.privacyStatus?.ordinal
    }

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
                                    selectedIndexes = listOf(newPrivacyIndex).mapNotNull { it },
                                    onSegmentClicked = { newPrivacyIndex = it },
                                    segmentModifier = Modifier
                                        .height(40.dp),
                                    modifier = Modifier
                                        .width(400.dp)
                                )
                            }

                            AppTextField(
                                label = "Title",
                                value = "$newTitle",
                                onValueChange = { newTitle = it },
                                supportingText = {
                                    AnimatedVisibility(visible = isTitleChanged) {
                                        Text(text = "Changed")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        videoService.generateTitle()?.let {
                                            newTitle = it
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(text = "Generate title")
                            }
                            AppTextField(
                                label = "Description",
                                value = "$newDescription",
                                onValueChange = { newDescription = it },
                                supportingText = {
                                    AnimatedVisibility(visible = isDescriptionChanged) {
                                        Text(text = "Changed")
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    scope.launch {
                                        val (desc, tags) = videoService.generateDescription()
                                        newDescription = desc
                                        newTags = tags
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(bottom = 8.dp)
                            ) {
                                Text(text = "Generate description")
                            }

                            Divider()

                            if (ytVideo.tags.isEmpty()) {
                                Text(text = "Tags: No tags")
                            }

                            Text(text = "NewTags: ${newTags?.joinToString(", ")}")

                            AnimatedVisibility(visible = isTagsChanged) {
                                Text(
                                    text = "Tags changed",
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }


                            Divider()

                            video.ytVideo?.localizations?.forEach { (s, pair) ->
                                Text(text = "Language code: $s")
                                Text(text = "Title: ${pair.first}")
                                Text(text = "Description: ${pair.second}")

                                Divider()
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Divider()

                            Text(text = "Local video id: ${video.localVideo?.id}")
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
                            Text(text = "Screenshots: ${video.localVideo?.screenshots}")
                            Text(text = "descriptions: ${video.localVideo?.descriptions}")
                            Text(text = "descriptionContext: ${video.localVideo?.descriptionContext}")
                            Text(text = "CreatedAt: ${video.localVideo?.createdAt?.pretty()}")
                            Column(
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                video.localVideo?.localizations?.forEach { (s, pair) ->
                                    Text(text = "Language code: $s")
                                    Text(text = "Title: ${pair.first}")
                                    Text(text = "Descriptions: ${pair.second}")
                                }
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
                            onClick = {
                                scope.launch {
                                    val tit = async(Dispatchers.Default) {
                                        videoService.generateTitle()
                                    }.await()
                                    val deferredDesc = async(Dispatchers.Default) {
                                        videoService.generateDescription()
                                    }
                                    val (desc, tags) = deferredDesc.await()
                                    tit?.let { newTitle = it }
                                    desc?.let { newDescription = desc }
                                    if (tags.isNotEmpty()) { newTags = tags }
                                }
                            },
                            enabled = true,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Generate all")
                        }
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    val privacy =
                                        newPrivacyIndex?.let {
                                            PrivacyStatus.values().toList().get(it)
                                        }
                                    videoService.updateLiveVideo(
                                        video = video,
                                        title = if (isTitleChanged) newTitle else null,
                                        description = if (isDescriptionChanged) newDescription else null,
                                        privacyStatus = if (isPrivacyChanged) privacy else null,
                                        tags = if (isTagsChanged) newTags else null,
                                    )
                                }
                            },
                            enabled = isTitleChanged || isDescriptionChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Update")
                        }
                        FilledTonalButton(
                            onClick = {
                                scope.launch {
                                    newTitle = video.ytVideo?.title ?: ""
                                    newDescription = video.ytVideo?.description ?: ""
                                }
                            },
                            enabled = isTitleChanged || isDescriptionChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(text = "Restore")
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
