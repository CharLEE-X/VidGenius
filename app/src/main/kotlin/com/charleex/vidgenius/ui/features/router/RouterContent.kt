package com.charleex.vidgenius.ui.features.router

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.dp
import com.charleex.vidgenius.datasource.VideoService
import com.charleex.vidgenius.datasource.feature.ConfigManager
import com.charleex.vidgenius.ui.components.AppVerticalScrollbar
import com.charleex.vidgenius.ui.components.NavigationSheet
import com.charleex.vidgenius.ui.components.TopBar
import com.charleex.vidgenius.ui.components.TopBarState
import com.charleex.vidgenius.ui.features.video_detail.VideoDetailContent
import com.charleex.vidgenius.ui.features.videos.VideosContent
import com.copperleaf.ballast.navigation.routing.Backstack
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.currentRouteOrNull
import com.copperleaf.ballast.navigation.routing.currentRouteOrThrow
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.pathParameter
import com.copperleaf.ballast.navigation.routing.renderCurrentDestination
import com.copperleaf.ballast.navigation.routing.stringPath
import com.copperleaf.ballast.navigation.vm.Router

@Composable
internal fun RouterContent(
    displayMessage: (String) -> Unit,
    videoService: VideoService,
    configManager: ConfigManager,
    window: ComposeWindow,
) {
    val scope = rememberCoroutineScope()
    val router: Router<RouterScreen> =
        remember(scope) {
            RouterViewModel(
                viewModelScope = scope,
                initialRoute = RouterScreen.Videos,
            )
        }
    val routerState: Backstack<RouterScreen> by router.observeStates().collectAsState()

    val listState = rememberLazyListState()
    var lazyListState: LazyListState by remember { mutableStateOf(listState) }
    var title: String? by remember { mutableStateOf(null) }
    var likeCount: Int? by remember { mutableStateOf(null) }
    var dislikeCount: Int? by remember { mutableStateOf(null) }
    var viewCount: Int? by remember { mutableStateOf(null) }
    var commentCount: Int? by remember { mutableStateOf(null) }
    var topBarState: TopBarState by remember { mutableStateOf(TopBarState.VideoList) }

    Surface(
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            NavigationSheet(
                routerScreen = routerState.currentRouteOrNull ?: RouterScreen.Videos,
                onGoToDashboard = {
                    router.trySend(
                        RouterContract.Inputs.GoToDestination(
                            RouterScreen.Dashboard
                                .directions()
                                .build()
                        )
                    )
                },
                onGoToGeneration = {
                    router.trySend(
                        RouterContract.Inputs.GoToDestination(
                            RouterScreen.Videos
                                .directions()
                                .build()
                        )
                    )
                },
            ) {
                routerState.renderCurrentDestination(
                    route = { routerScreen: RouterScreen ->
                        when (routerScreen) {
                            RouterScreen.Dashboard -> {

                            }

                            RouterScreen.Videos -> {
                                title = null
                                topBarState = TopBarState.VideoList

                                VideosContent(
                                    videoService = videoService,
                                    configManager = configManager,
                                    window = window,
                                    displayMessage = displayMessage,
                                    onItemClicked = { videoId ->
                                        router.trySend(
                                            RouterContract.Inputs.GoToDestination(
                                                RouterScreen.VideoDetail
                                                    .directions()
                                                    .pathParameter("videoId", videoId)
                                                    .build()
                                            )
                                        )
                                    },
                                    scroll = { lazyListState = it },
                                )
                            }

                            RouterScreen.VideoDetail -> {
                                val videoId by stringPath("videoId")
                                val video by videoService.getVideo(videoId).collectAsState()
                                videoService.getVideoDetails(video)
                                title = video.ytVideo?.id
                                topBarState = TopBarState.VideoDetail
                                likeCount = video.ytVideo?.likeCount
                                dislikeCount = video.ytVideo?.dislikeCount
                                viewCount = video.ytVideo?.viewCount
                                commentCount = video.ytVideo?.commentCount

                                VideoDetailContent(
                                    video = video,
                                    videoService = videoService,
                                    scroll = { lazyListState = it },
                                )
                            }
                        }
                    },
                    notFound = { },
                )
                TopBar(
                    title = title,
                    likeCount = likeCount,
                    dislikeCount = dislikeCount,
                    viewCount = viewCount,
                    commentCount = commentCount,
                    configManager = configManager,
                    onBackClicked = {
                        router.trySend(RouterContract.Inputs.GoBack())
                    },
                    topBarState = topBarState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                AppVerticalScrollbar(
                    adapter = rememberScrollbarAdapter(lazyListState),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
