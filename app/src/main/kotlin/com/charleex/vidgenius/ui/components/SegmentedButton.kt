package com.charleex.vidgenius.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.AppTheme

enum class SegmentType { START, INNER, SINGLE, END }
enum class SegmentShape { ROUNDED, CUT }
enum class SegmentOrientation { HORIZONTAL, VERTICAL }

data class SegmentCorners(
    val shape: SegmentShape,
    val topStart: Dp,
    val topEnd: Dp,
    val bottomStart: Dp,
    val bottomEnd: Dp,
    val contentPaddingValues: PaddingValues,
)

data class SegmentColors(
    val backgroundColorSelected: Color,
    val backgroundColorUnselected: Color,
    val borderColorSelected: Color,
    val borderColorUnselected: Color,
    val contentColorSelected: Color,
    val contentColorUnselected: Color,
)

object SegmentDefaults {
    fun defaultCorners(
        shape: SegmentShape = SegmentShape.ROUNDED,
        topStart: Dp = 20.dp,
        topEnd: Dp = 20.dp,
        bottomStart: Dp = 20.dp,
        bottomEnd: Dp = 20.dp,
        contentPaddingValues: PaddingValues = PaddingValues(
            vertical = 6.dp,
            horizontal = 8.dp,
        ),
    ) = SegmentCorners(
        shape = shape,
        topStart = topStart,
        topEnd = topEnd,
        bottomStart = bottomStart,
        bottomEnd = bottomEnd,
        contentPaddingValues = contentPaddingValues,
    )

    @Composable
    fun defaultColors(
        backgroundColorSelected: Color = MaterialTheme.colorScheme.primary,
        backgroundColorUnselected: Color = MaterialTheme.colorScheme.surface,
        borderColorSelected: Color = MaterialTheme.colorScheme.primary,
        borderColorUnselected: Color = MaterialTheme.colorScheme.primary,
        contentColorSelected: Color = MaterialTheme.colorScheme.onPrimary,
        contentColorUnselected: Color = MaterialTheme.colorScheme.onSurface,
    ) = SegmentColors(
        backgroundColorSelected = backgroundColorSelected,
        backgroundColorUnselected = backgroundColorUnselected,
        borderColorSelected = borderColorSelected,
        borderColorUnselected = borderColorUnselected,
        contentColorSelected = contentColorSelected,
        contentColorUnselected = contentColorUnselected,
    )
}

data class SegmentSpec(
    val label: String,
    val icon: ImageVector? = null,
    val borderWidth: Dp = 1.dp,
    val tonalElevation: Dp = 1.dp,
    val shadowElevation: Dp = 0.dp,
    val iconPadding: Dp = 8.dp,
    val colors: SegmentColors,
    val corners: SegmentCorners,
)

@Composable
fun SegmentsGroup(
    modifier: Modifier = Modifier,
    segmentModifier: Modifier = Modifier,
    segments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSegmentClicked: (Int) -> Unit,
    segmentOrientation: SegmentOrientation = SegmentOrientation.HORIZONTAL,
) {
    when (segmentOrientation) {
        SegmentOrientation.HORIZONTAL -> {
            SegmentsGroupHorizontal(
                modifier = modifier,
                segmentModifier = segmentModifier,
                segments = segments,
                selectedIndexes = selectedIndexes,
                onSegmentSelected = onSegmentClicked,
            )
        }

        SegmentOrientation.VERTICAL -> {
            SegmentsGroupVertical(
                modifier = modifier,
                segmentModifier = segmentModifier,
                segments = segments,
                selectedIndexes = selectedIndexes,
                onSegmentSelected = onSegmentClicked,
            )
        }
    }
}

@Composable
private fun SegmentsGroupHorizontal(
    modifier: Modifier,
    segmentModifier: Modifier,
    segments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSegmentSelected: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        SegmentsLine(
            segmentModifier = segmentModifier.weight(1f),
            segments = segments,
            selectedIndexes = selectedIndexes,
            onSegmentSelected = onSegmentSelected,
        )
    }
}

@Composable
private fun SegmentsGroupVertical(
    modifier: Modifier,
    segmentModifier: Modifier = Modifier,
    segments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSegmentSelected: (Int) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        SegmentsLine(
            segmentModifier = segmentModifier.weight(1f),
            segments = segments,
            selectedIndexes = selectedIndexes,
            onSegmentSelected = onSegmentSelected,
        )
    }
}

@Composable
private fun SegmentsLine(
    segmentModifier: Modifier = Modifier,
    segments: List<SegmentSpec>,
    selectedIndexes: List<Int>,
    onSegmentSelected: (Int) -> Unit,
) = when (segments.size) {
    0 -> {
        Text(
            text = "No segments",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error,
        )
    }

    1 -> {
        val item = segments[0]
        Segment(
            label = item.label,
            icon = item.icon,
            iconPadding = item.iconPadding,
            borderWidth = item.borderWidth,
            tonalElevation = item.tonalElevation,
            shadowElevation = item.shadowElevation,
            segmentType = SegmentType.SINGLE,
            segmentColors = item.colors,
            segmentCorners = item.corners,
            modifier = segmentModifier,
            selected = 0 in selectedIndexes,
            onClick = { onSegmentSelected(0) },
        )
    }

    2 -> {
        val item1 = segments[0]
        Segment(
            label = item1.label,
            icon = item1.icon,
            iconPadding = item1.iconPadding,
            borderWidth = item1.borderWidth,
            tonalElevation = item1.tonalElevation,
            shadowElevation = item1.shadowElevation,
            segmentType = SegmentType.START,
            segmentColors = item1.colors,
            segmentCorners = item1.corners,
            modifier = segmentModifier,
            selected = 0 in selectedIndexes,
            onClick = { onSegmentSelected(0) },
        )
        val item2 = segments[1]
        Segment(
            label = item2.label,
            icon = item2.icon,
            iconPadding = item2.iconPadding,
            borderWidth = item2.borderWidth,
            tonalElevation = item2.tonalElevation,
            shadowElevation = item2.shadowElevation,
            segmentType = SegmentType.END,
            segmentColors = item2.colors,
            segmentCorners = item2.corners,
            modifier = segmentModifier,
            selected = 1 in selectedIndexes,
            onClick = { onSegmentSelected(1) },
        )
    }

    else -> {
        val itemFirst = segments[0]
        Segment(
            label = itemFirst.label,
            icon = itemFirst.icon,
            iconPadding = itemFirst.iconPadding,
            borderWidth = itemFirst.borderWidth,
            tonalElevation = itemFirst.tonalElevation,
            shadowElevation = itemFirst.shadowElevation,
            segmentType = SegmentType.START,
            segmentColors = itemFirst.colors,
            segmentCorners = itemFirst.corners,
            modifier = segmentModifier,
            selected = 0 in selectedIndexes,
            onClick = { onSegmentSelected(0) },
        )
        segments.subList(1, segments.size - 1).forEachIndexed { index, segment ->
            Segment(
                label = segment.label,
                icon = segment.icon,
                iconPadding = segment.iconPadding,
                borderWidth = segment.borderWidth,
                tonalElevation = segment.tonalElevation,
                shadowElevation = segment.shadowElevation,
                segmentType = SegmentType.INNER,
                segmentColors = segment.colors,
                segmentCorners = segment.corners,
                modifier = segmentModifier,
                selected = (index + 1) in selectedIndexes,
                onClick = { onSegmentSelected(index + 1) },
            )
        }
        val itemLast = segments[segments.size - 1]
        Segment(
            label = itemLast.label,
            icon = itemLast.icon,
            iconPadding = itemLast.iconPadding,
            borderWidth = itemLast.borderWidth,
            tonalElevation = itemLast.tonalElevation,
            shadowElevation = itemLast.shadowElevation,
            segmentType = SegmentType.END,
            segmentColors = itemLast.colors,
            segmentCorners = itemLast.corners,
            modifier = segmentModifier,
            selected = (segments.size - 1) in selectedIndexes,
            onClick = { onSegmentSelected(segments.size - 1) },
        )
    }
}

@Composable
private fun Segment(
    modifier: Modifier,
    label: String,
    segmentType: SegmentType,
    icon: ImageVector?,
    iconPadding: Dp,
    borderWidth: Dp,
    selected: Boolean,
    tonalElevation: Dp,
    shadowElevation: Dp,
    onClick: () -> Unit,
    segmentCorners: SegmentCorners,
    segmentColors: SegmentColors,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val contentScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(
            durationMillis = 100,
            easing = FastOutSlowInEasing,
        ),
    )

    val topStartCorner by animateDpAsState(if (isPressed) (segmentCorners.topStart + 1.dp) else segmentCorners.topStart)
    val topEndCorner by animateDpAsState(if (isPressed) (segmentCorners.topEnd + 1.dp) else segmentCorners.topEnd)
    val bottomStartCorner by animateDpAsState(if (isPressed) (segmentCorners.bottomStart + 1.dp) else segmentCorners.bottomStart)
    val bottomEndCorner by animateDpAsState(if (isPressed) (segmentCorners.bottomEnd + 1.dp) else segmentCorners.bottomEnd)

    val cutShape = CutCornerShape(
        topStart = when (segmentType) {
            SegmentType.START -> topStartCorner
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> topStartCorner
            SegmentType.END -> 0.dp
        },
        bottomStart = when (segmentType) {
            SegmentType.START -> bottomStartCorner
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> bottomStartCorner
            SegmentType.END -> 0.dp
        },
        topEnd = when (segmentType) {
            SegmentType.START -> 0.dp
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> topEndCorner
            SegmentType.END -> topEndCorner
        },
        bottomEnd = when (segmentType) {
            SegmentType.START -> 0.dp
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> bottomEndCorner
            SegmentType.END -> bottomEndCorner
        },
    )

    val roundedShape = RoundedCornerShape(
        topStart = when (segmentType) {
            SegmentType.START -> segmentCorners.topStart
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> segmentCorners.topStart
            SegmentType.END -> 0.dp
        },
        bottomStart = when (segmentType) {
            SegmentType.START -> segmentCorners.bottomStart
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> segmentCorners.bottomStart
            SegmentType.END -> 0.dp
        },
        topEnd = when (segmentType) {
            SegmentType.START -> 0.dp
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> segmentCorners.topEnd
            SegmentType.END -> segmentCorners.topEnd
        },
        bottomEnd = when (segmentType) {
            SegmentType.START -> 0.dp
            SegmentType.INNER -> 0.dp
            SegmentType.SINGLE -> segmentCorners.bottomEnd
            SegmentType.END -> segmentCorners.bottomEnd
        },
    )

    val translationX = when (segmentType) {
        SegmentType.START -> borderWidth.value

        SegmentType.INNER,
        SegmentType.SINGLE,
        -> 0F

        SegmentType.END -> -borderWidth.value
    }

    val backgroundColor by animateColorAsState(
        targetValue = if (selected)
            segmentColors.backgroundColorSelected else segmentColors.backgroundColorUnselected,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        )
    )

    val borderColor by animateColorAsState(
        targetValue = if (selected)
            segmentColors.borderColorSelected else segmentColors.borderColorUnselected,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        )
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) segmentColors.contentColorSelected else segmentColors.contentColorUnselected,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing,
        )
    )

    val shape = when (segmentCorners.shape) {
        SegmentShape.ROUNDED -> roundedShape
        SegmentShape.CUT -> cutShape
    }

    Surface(
        shape = shape,
        border = BorderStroke(
            width = borderWidth,
            color = borderColor
        ),
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        color = backgroundColor,
        modifier = modifier
            .graphicsLayer(
                translationX = translationX,
            )
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = interactionSource,
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .padding(
                        horizontal = 8.dp,
                        vertical = 6.dp,
                    )
                    .graphicsLayer(
                        scaleX = contentScale,
                        scaleY = contentScale,
                    )
            ) {
                icon?.let {
                    Icon(
                        imageVector = icon,
                        tint = contentColor,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = iconPadding),
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SegmentedButtonPreview() {
    Surface(
        tonalElevation = 0.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            PreviewItem(
                darkTheme = false,
                modifier = Modifier
                    .weight(1f)
            )
            PreviewItem(
                darkTheme = true,
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun PreviewItem(
    modifier: Modifier,
    darkTheme: Boolean,
) {
    AppTheme(
        useDarkTheme = darkTheme
    ) {
        Surface(
            modifier = modifier
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    SegmentsGroup(
                        segments = listOf(
                            SegmentSpec(
                                label = "First",
                                icon = Icons.Default.ArrowBack,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                            SegmentSpec(
                                label = "Second",
                                icon = Icons.Default.Home,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                            SegmentSpec(
                                label = "Third",
                                icon = Icons.Default.ArrowForward,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                        ),
                        selectedIndexes = listOf(1, 2),
                        onSegmentClicked = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                    )
                    SegmentsGroup(
                        segments = listOf(
                            SegmentSpec(
                                label = "First",
                                icon = Icons.Default.ArrowBack,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                            SegmentSpec(
                                label = "Second",
                                icon = Icons.Default.Home,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                            SegmentSpec(
                                label = "Third",
                                icon = Icons.Default.ArrowForward,
                                colors = SegmentDefaults.defaultColors(),
                                corners = SegmentDefaults.defaultCorners(),
                            ),
                        ),
                        selectedIndexes = listOf(2, 3),
                        onSegmentClicked = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                    )
                }
            }
        }
    }
}
