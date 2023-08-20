package com.charleex.vidgenius.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.ui.features.router.RouterScreen

@Composable
fun NavigationSheet(
    routerScreen: RouterScreen,
    onGoToDashboard: () -> Unit,
    onGoToGeneration: () -> Unit,
    onGoToTwits: () -> Unit,
    block: @Composable BoxScope.() -> Unit,
) {
    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            PermanentNavigationDrawer(
                drawerContent = {
                    PermanentDrawerSheet {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            Spacer(modifier = Modifier.size(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp)
                            ) {
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
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.size(24.dp))

                            ElevatedButton(
                                shape = MaterialTheme.shapes.medium,
                                contentPadding = PaddingValues(8.dp),
                                onClick = onGoToDashboard,
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
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
                                text = "GENERATION",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Psychology,
                                        contentDescription = null
                                    )
                                },
                                label = { Text("Videos") },
                                selected = routerScreen == RouterScreen.Videos,
                                onClick = onGoToGeneration,
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                ),
                                modifier = Modifier
                            )
                            Divider(
                                color = if (isSystemInDarkTheme())
                                    Color.DarkGray else Color.White,
                                thickness = Dp.Hairline,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 28.dp, bottom = 12.dp)
                            )

                            Text(
                                text = "TWITTER",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            NavigationDrawerItem(
                                icon = {
                                    Image(
                                        painter = painterResource("twitter.png"),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                    )
                                },
                                label = { Text("Tweets") },
                                selected = routerScreen == RouterScreen.Twits,
                                onClick = onGoToTwits,
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                ),
                                modifier = Modifier
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                },
            ) {
                block()
            }
        }
    }
}
