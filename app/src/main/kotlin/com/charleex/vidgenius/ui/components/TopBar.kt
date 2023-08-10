package com.charleex.vidgenius.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CarCrash
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
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
    configManager: ConfigManager,
    tonalElevation: Dp = 1.dp,
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
    val selectedIndexes by remember { mutableStateOf(listOf(selectedIndex)) }

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
            onSetConfig = {
                scope.launch {
                    configManager.setYtConfig(it)
                }
            },
            modifier = Modifier
                .padding(top = configPanelTopPadding)
                .fillMaxWidth()
                .alpha(configPanelAlpha)
        )
        TopBarContent(
            modifier = modifier,
            showConfigs = showConfigs,
            categorySegments = categorySegments,
            selectedIndexes = selectedIndexes,
            onSelected = {
                scope.launch {
                    val index = it % categorySegments.size
                    val category = allCategories.first { it.title == categorySegments[index].label }
                    configManager.setCategory(category)
                }
            },
            tonalElevation = tonalElevation,
            onShowConfig = { showConfigs = !showConfigs },
        )
    }
}

@Composable
private fun TopBarContent(
    modifier: Modifier = Modifier,
    showConfigs: Boolean,
    categorySegments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSelected: (Int) -> Unit,
    tonalElevation: Dp,
    onShowConfig: () -> Unit,
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
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 72.dp,
                    end = 48.dp,
                )
                .padding(vertical = paddingVertical)
        ) {
            Row {
                Text(
                    text = "VID",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.secondary,
                )
                Text(
                    text = "GENIUS",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            SettingsButton(
                onShowConfig = onShowConfig,
            )
            SegmentsGroup(
                segments = categorySegments,
                selectedIndexes = selectedIndexes,
                onSegmentClicked = onSelected,
                modifier = Modifier
                    .width(400.dp)
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
