package com.charleex.vidgenius.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.datasource.feature.youtube.model.YtConfig
import com.charleex.vidgenius.datasource.feature.youtube.model.allCategories
import com.charleex.vidgenius.datasource.feature.youtube.model.ytConfigs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String?,
    likeCount: Int?,
    dislikeCount: Int?,
    viewCount: Int?,
    commentCount: Int?,
    topBarState: TopBarState,
    configManager: ConfigManager,
    tonalElevation: Dp = 1.dp,
    onBackClicked: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val config by configManager.config.collectAsState()
    val currentConfigId = config.ytConfig?.id
    val selectedCategory = config.category.title
    val categoryAnimals = allCategories.first { it.title == "Animals" }
    val categoryFails = allCategories.first { it.title == "Fails" }
    val categoryHacks = allCategories.first { it.title == "Hacks" }
    val categoriesWithIcons = mapOf(
        categoryAnimals.title to Icons.Default.Pets,
        categoryFails.title to Icons.Default.CarCrash,
        categoryFails.title to Icons.Default.CarCrash,
        categoryHacks.title to Icons.Default.LaptopMac,
    )

    val categorySegments = categoriesWithIcons.map { (name, icon) ->
        SegmentSpec(
            icon = icon,
            label = name,
            colors = SegmentDefaults.defaultColors(),
            corners = SegmentDefaults.defaultCorners(),
        )
    }
    val selectedIndex = categorySegments.indexOfFirst { it.label == selectedCategory }

    var showConfigs by remember { mutableStateOf(false) }

    val configPanelTopPadding by animateDpAsState(
        targetValue = if (showConfigs) 64.dp else 0.dp,
        animationSpec = tween(500)
    )
    val configPanelAlpha by animateFloatAsState(
        targetValue = if (showConfigs) 1f else 0f,
        animationSpec = tween(300)
    )

    LaunchedEffect(Unit) {
        println("currentConfigId: ${currentConfigId == null}")
        if (currentConfigId == null) {
            delay(700)
            showConfigs = true
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxWidth()
    ) {
        ConfigPanel(
            tonalElevation = tonalElevation,
            ytConfigs = ytConfigs,
            currentConfigId = currentConfigId,
            onSetConfig = { ytConfig ->
                scope.launch {
                    if (configManager.setYtConfig(ytConfig)) {
                        showConfigs = false
                    }
                }
            },
            modifier = Modifier
                .padding(top = configPanelTopPadding)
                .fillMaxWidth()
                .alpha(configPanelAlpha)
        )
        TopBarContent(
            modifier = modifier,
            title = title,
            likeCount = likeCount,
            dislikeCount = dislikeCount,
            viewCount = viewCount,
            commentCount = commentCount,
            showConfigs = showConfigs,
            categorySegments = categorySegments,
            selectedIndexes = listOf(selectedIndex),
            onSelected = {
                scope.launch {
                    val index = it % categorySegments.size
                    val category = allCategories.first { it.title == categorySegments[index].label }
                    configManager.setCategory(category)
                }
            },
            tonalElevation = tonalElevation,
            onShowConfig = { showConfigs = !showConfigs },
            topBarState = topBarState,
            onBackClicked = onBackClicked,
        )
    }
}

enum class TopBarState {
    VideoList,
    VideoDetail,
}

@Composable
private fun TopBarContent(
    modifier: Modifier = Modifier,
    title: String?,
    likeCount: Int?,
    dislikeCount: Int?,
    viewCount: Int?,
    commentCount: Int?,
    showConfigs: Boolean,
    categorySegments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSelected: (Int) -> Unit,
    tonalElevation: Dp,
    onShowConfig: () -> Unit,
    topBarState: TopBarState,
    onBackClicked: () -> Unit,
) {
    val bgTonalElevation by animateDpAsState(
        targetValue = if (showConfigs) (tonalElevation + 1.dp) else (tonalElevation + 3.dp),
        animationSpec = tween(300)
    )

    val paddingVertical by animateDpAsState(
        targetValue = if (showConfigs) 8.dp else 24.dp,
        animationSpec = tween(300)
    )

    Surface(
        tonalElevation = bgTonalElevation,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        when (topBarState) {
            TopBarState.VideoList -> {
                TopBarVideoList(
                    paddingVertical = paddingVertical,
                    onShowConfig = onShowConfig,
                    categorySegments = categorySegments,
                    selectedIndexes = selectedIndexes,
                    onSelected = onSelected,
                    modifier = Modifier
                )
            }

            TopBarState.VideoDetail -> {
                TopBarVideoDetail(
                    title = title,
                    likeCount = likeCount,
                    dislikeCount = dislikeCount,
                    viewCount = viewCount,
                    commentCount = commentCount,
                    onBackClicked = onBackClicked,
                    paddingVertical = paddingVertical,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun TopBarVideoList(
    modifier: Modifier = Modifier,
    paddingVertical: Dp,
    onShowConfig: () -> Unit,
    categorySegments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSelected: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 72.dp,
                end = 48.dp,
            )
            .padding(vertical = paddingVertical)
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.weight(1f))
        SettingsButton(
            onShowConfig = onShowConfig,
        )
        SegmentsGroup(
            segments = categorySegments,
            selectedIndexes = selectedIndexes,
            onSegmentClicked = onSelected,
            segmentModifier = Modifier
                .height(40.dp),
            modifier = Modifier
                .width(400.dp)
        )
    }
}

@Composable
private fun TopBarVideoDetail(
    title: String?,
    likeCount: Int?,
    dislikeCount: Int?,
    viewCount: Int?,
    commentCount: Int?,
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit,
    paddingVertical: Dp,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 72.dp,
                end = 48.dp,
            )
            .padding(vertical = paddingVertical)
            .animateContentSize()
    ) {
        FilledTonalIconButton(
            onClick = onBackClicked,
            modifier = Modifier
                .size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
            )
        }
        Row {
            Text(
                text = "YouTube ID:",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = title ?: "N/A",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        StatItem(
            value = likeCount,
            icon = Icons.Default.ThumbUp,
            modifier = Modifier
        )
        StatItem(
            value = dislikeCount,
            icon = Icons.Default.ThumbDown,
            modifier = Modifier
        )
        StatItem(
            value = commentCount,
            icon = Icons.Default.Comment,
            modifier = Modifier
        )
        StatItem(
            value = viewCount,
            icon = Icons.Default.Watch,
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Composable
internal fun StatItem(
    modifier: Modifier = Modifier,
    value: Int?,
    icon: ImageVector,
) {
    AnimatedVisibility(value != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SettingsButton(
    onShowConfig: () -> Unit,
) {
    var isHovered by remember {
        mutableStateOf(false)
    }
    var rotation by remember { mutableStateOf(0f) }
    val rotationState by animateFloatAsState(if (isHovered) rotation else 0f)
    if (isHovered) {
        val transition = rememberInfiniteTransition()
        val settingsRotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Restart
            )
        )

        LaunchedEffect(settingsRotation) {
            rotation = settingsRotation
        }
    } else {
        rotation = 0f
    }

    IconButton(
        onClick = onShowConfig,
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .rotate(rotationState)
                .onPointerEvent(
                    eventType = PointerEventType.Enter,
                    onEvent = { isHovered = true }
                )
                .onPointerEvent(
                    eventType = PointerEventType.Exit,
                    onEvent = { isHovered = false }
                )
        )
    }
}

@Composable
private fun ConfigPanel(
    modifier: Modifier = Modifier,
    ytConfigs: List<YtConfig>,
    currentConfigId: String?,
    tonalElevation: Dp,
    shadowElevation: Dp = 0.dp,
    onSetConfig: (YtConfig) -> Unit,
) {
    Surface(
        tonalElevation = tonalElevation + 6.dp,
        shadowElevation = shadowElevation,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Select a config",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 4.dp,
                ),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            ) {
                ytConfigs.forEach { ytConfig ->
                    val containerColor by animateColorAsState(
                        targetValue = if (ytConfig.id == currentConfigId)
                            MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surface,
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (ytConfig.id == currentConfigId)
                            MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary
                    )

                    OutlinedButton(
                        onClick = { onSetConfig(ytConfig) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = containerColor,
                            contentColor = contentColor,
                        ),
                    ) {
                        Text(
                            text = ytConfig.title,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}
