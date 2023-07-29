package com.charleex.vidgenius.ui.features

import androidx.compose.runtime.Composable
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun ScreenshotToTextContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    videoId: String,
) {
//    val scope = rememberCoroutineScope()
//    val vm = remember(scope) {
//        ScreenshotsToTextViewModel(
//            scope = scope,
//            showMessage = displayMessage,
//            videoId = videoId
//        )
//    }
//    val state by vm.observeStates().collectAsState()
//    val layColumnState = rememberLazyListState()
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        LazyColumn(
//            state = layColumnState,
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//            contentPadding = PaddingValues(
//                top = 32.dp,
//                bottom = 32.dp,
//            ),
//            modifier = Modifier
//                .fillMaxSize()
//                .animateContentSize()
//        ) {
//            item {
//                Row {
//                Text(
//                    text = state.video.path,
//                    color = MaterialTheme.colors.onSurface,
//                )
//                    Text(
//                        text = state.progress.toString(),
//                        color = MaterialTheme.colors.onSurface,
//                    )
//                    Text(
//                        text = state.processing.toString(),
//                        color = MaterialTheme.colors.onSurface,
//                    )
//                }
//            }
//            item {
//                Column(
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                ) {
//                    state.video.screenshots.forEach { screenshot ->
//                        Text(
//                            text = screenshot.path,
//                            color = MaterialTheme.colors.onSurface,
//                        )
//                        screenshot.description?.let {
//                            Text(
//                                text = it,
//                                color = MaterialTheme.colors.onSurface,
//                            )
//                        }
//                    }
//                }
//            }
//        }
//        VerticalScrollbar(
//            modifier = Modifier
//                .align(Alignment.CenterEnd)
//                .fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(layColumnState)
//        )
//    }
}
