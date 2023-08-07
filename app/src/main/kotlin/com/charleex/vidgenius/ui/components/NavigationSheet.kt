package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.model.ChannelConfig
import com.charleex.vidgenius.ui.features.router.RouterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationSheet(
    routerScreen: RouterScreen,
    onGoToAnimalUploads: () -> Unit,
    onGoToAnimalGeneration: () -> Unit,
    onGoToAnimalSubtitles: () -> Unit,
    onGoToFailsUploads: () -> Unit,
    onGoToFailsGeneration: () -> Unit,
    onGoToFailsSubtitles: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToDashboard: () -> Unit,
    block: @Composable () -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Spacer(modifier = Modifier.size(24.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "VIDGENIUS",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                            )
                        }

                        FilledTonalButton(
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(8.dp),
                            onClick = onGoToDashboard,
                            modifier = Modifier
                                .padding(top = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.CenterStart)
                                        .padding(start = 12.dp)
                                )
                                Text(
                                    text = "Dashboard",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(12.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.size(32.dp))

                        Text(
                            text = ChannelConfig.Animals().title,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Uploads") },
                            selected = routerScreen == RouterScreen.AnimalsUploads,
                            onClick = onGoToAnimalUploads,
                            modifier = Modifier,
                        )
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Generation") },
                            selected = routerScreen == RouterScreen.AnimalsGeneration,
                            onClick = onGoToAnimalGeneration,
                            modifier = Modifier
                        )
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Subtitles") },
                            selected = routerScreen == RouterScreen.AnimalsSubtitles,
                            onClick = onGoToAnimalSubtitles,
                            modifier = Modifier
                        )

                        Divider(
                            color = Color.White,
                            thickness = 1.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )

                        Text(
                            text = ChannelConfig.Fails().title,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Upload,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Uploads") },
                            selected = routerScreen == RouterScreen.FailsUploads,
                            onClick = onGoToFailsUploads,
                            modifier = Modifier,
                        )
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Generation") },
                            selected = routerScreen == RouterScreen.FailsGeneration,
                            onClick = onGoToFailsGeneration,
                            modifier = Modifier
                        )
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Subtitles,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Subtitles") },
                            selected = routerScreen == RouterScreen.FailsSubtitles,
                            onClick = onGoToFailsSubtitles,
                            modifier = Modifier
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Settings") },
                            selected = routerScreen == RouterScreen.Settings,
                            onClick = onGoToSettings,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            },
        ) {
            block()
        }
    }
}
