package com.charleex.vidgenius.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.UploadsManager
import com.charleex.vidgenius.datasource.VideoProcessing
import com.charleex.vidgenius.datasource.feature.youtube.PrivacyStatus
import com.charleex.vidgenius.datasource.model.ChannelConfig
import com.charleex.vidgenius.ui.features.router.RouterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationSheet(
    routerScreen: RouterScreen,
    animalsVideoProcessing: VideoProcessing,
    failsVideoProcessing: VideoProcessing,
    animalsUploadsManager: UploadsManager,
    failsUploadsManager: UploadsManager,
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
    val animalYtItems by animalsUploadsManager.ytVideos.collectAsState(emptyList())
    val failsYtItems by failsUploadsManager.ytVideos.collectAsState(emptyList())

    val animalVideos by animalsVideoProcessing.videos.collectAsState(emptyList())
    val failsVideos by failsVideoProcessing.videos.collectAsState(emptyList())


    val animalsToGenerate =
        animalYtItems.filter { it.title in animalVideos.map { it.youtubeName } }

    val failsToGenerate =
        failsYtItems.filter { it.title in failsVideos.map { it.youtubeName } }

    val privateStatuses = listOf(PrivacyStatus.PRIVATE.value, PrivacyStatus.UNLISTED.value)
    val privateAnimals = animalYtItems.filter { it.privacyStatus in privateStatuses }
    val privateFails = failsYtItems.filter { it.privacyStatus in privateStatuses }

    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .padding(horizontal = 24.dp)

                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.size(32.dp))

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

                        Spacer(modifier = Modifier.size(32.dp))

                        FilledTonalButton(
                            shape = MaterialTheme.shapes.medium,
                            contentPadding = PaddingValues(16.dp),
                            onClick = onGoToDashboard,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterStart)
                                )
                                Text(
                                    text = "Dashboard",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.size(32.dp))

                        Text(
                            text = ChannelConfig.Animals().title,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.size(24.dp))
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
                            badge = {
                                AnimatedVisibility(animalYtItems.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = animalYtItems.size.toString(),
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
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
                            modifier = Modifier,
                            badge = {
                                AnimatedVisibility(privateAnimals.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = privateAnimals.size.toString(),
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
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
                            color = if (isSystemInDarkTheme()) Color.White.copy(alpha = .3f) else Color.White,
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
                            badge = {
                                AnimatedVisibility(failsYtItems.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = failsYtItems.size.toString(),
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
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
                            modifier = Modifier,
                            badge = {
                                AnimatedVisibility(privateFails.isNotEmpty()) {
                                    Badge(
                                        modifier = Modifier
                                    ) {
                                        Text(
                                            text = privateFails.size.toString(),
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
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
