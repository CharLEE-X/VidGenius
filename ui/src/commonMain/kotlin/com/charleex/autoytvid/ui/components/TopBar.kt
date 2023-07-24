package com.charleex.autoytvid.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.charleex.autoytvid.feature.router.RouterScreen
import com.charleex.autoytvid.ui.AppState
import com.charleex.autoytvid.ui.util.icon

@Composable
internal fun AppTopBar(
    routerScreen: RouterScreen?,
    onBackClicked: () -> Unit,
    backEnabled: Boolean,
    extrasEnd: @Composable RowScope.() -> Unit,
) {
    val alphaState by animateFloatAsState(if (backEnabled) 1f else 0f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .graphicsLayer(shadowElevation = 4f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.run { spacedBy(16.dp) },
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = AppState.safePaddingValues.calculateTopPadding())
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = "Back",
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        enabled = backEnabled,
                        onClick = onBackClicked,
                        role = Role.Button,
                    )
                    .alpha(alphaState)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = routerScreen.icon(),
                contentDescription = "Back",
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        enabled = backEnabled,
                        onClick = onBackClicked,
                        role = Role.Button,
                    )
                    .alpha(alphaState)
            )
            Text(
                text = routerScreen?.name ?: "Unknown",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onBackground),
            )
            Spacer(modifier = Modifier.weight(1f))
            extrasEnd()
        }
    }
}
