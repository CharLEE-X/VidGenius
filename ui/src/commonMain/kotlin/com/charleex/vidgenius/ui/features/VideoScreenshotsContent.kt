package com.charleex.vidgenius.ui.features

import androidx.compose.runtime.Composable
import com.charleex.vidgenius.ui.util.Breakpoint

@Composable
internal fun VideoScreenshotsContent(
    breakpoint: Breakpoint,
    displayMessage: (String) -> Unit,
    videoId: String,
    goToScreenshotsToText: (videoId: String) -> Unit,
) {
//    val scope = rememberCoroutineScope()
//    val vm = remember(scope) {
//        VideoScreenshotsViewModel(
//            scope = scope,
//            videoId = videoId,
//            showMessage = displayMessage,
//        )
//    }
//    val state by vm.observeStates().collectAsState()
//    val layColumnState = rememberLazyListState()
//    var currentTimestamp by remember { mutableStateOf(0L) }
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
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                Text(
//                    text = state.video.path,
//                    color = MaterialTheme.colors.onSurface,
//                )
//                    Button(
//                        onClick = { goToScreenshotsToText(state.video.id) },
//                        modifier = Modifier.padding(start = 32.dp)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.TextSnippet,
//                            contentDescription = "Go to screenshots to text",
//                        )
//                        Text(
//                            text = "Go to screenshots to text",
//                            color = MaterialTheme.colors.onSurface,
//                        )
//                    }
//                }
//            }
//            state.video.description?.let { description ->
//                item {
//                    Text(
//                        text = description,
//                        color = MaterialTheme.colors.onSurface,
//                    )
//                }
//            }
////            item {
////                Player(
////                    url = state.video.path,
////                    onTimestampChange = { currentTimestamp = it },
////                    modifier = Modifier
////                        .fillMaxWidth()
////                        .height(300.dp),
////                )
////            }
////            item {
////                Column {
////                    Row(
////                        verticalAlignment = Alignment.CenterVertically,
////                    ) {
////                    Text(
////                        text = "Screenshot timestamps",
////                        color = MaterialTheme.colors.onSurface,
////                    )
////                     Button(
////                        onClick = { vm.trySend(VideoScreenshotsContract.Inputs.SaveTimestamp(timestamp = currentTimestamp)) },
////                        modifier = Modifier
////                     )   {
////                        Icon(
////                            imageVector = Icons.Default.Add,
////                            contentDescription = "Save timestamp",
////                        )
////                        Text(
////                            text = "Save timestamp",
////                            color = MaterialTheme.colors.onSurface,
////                        )
////                     }
////                    }
////                    Row {
////                        state.timestamps.forEach {
////                            Text(
////                                text = "TS: ${it}%",
////                                color = MaterialTheme.colors.onSurface,
////                            )
////                        }
////                    }
////                }
////            }
//            item {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(top = 24.dp)
//                        .animateContentSize()
//                ) {
//                    AnimatedVisibility(visible = !state.processing && (state.video.screenshots.size < state.timestamps.size)) {
//                        Button(
//                            onClick = { vm.trySend(VideoScreenshotsContract.Inputs.CaptureScreenshots) },
//                            modifier = Modifier
//                        ) {
//                            Icon(
//                                imageVector = Icons.Default.Screenshot,
//                                contentDescription = "Get screenshots",
//                            )
//                            Text(
//                                text = "Get screenshots",
//                                color = MaterialTheme.colors.onSurface,
//                            )
//                        }
//                    }
//                    AnimatedVisibility(visible = state.processing && state.video.screenshots.isEmpty()) {
//                        Box(
//                            contentAlignment = Alignment.Center,
//                            modifier = Modifier
//                                .fillMaxSize()
//                        ) {
//                            CircularProgressIndicator()
//                        }
//                    }
//                    LazyRow(
//                        horizontalArrangement = Arrangement.SpaceEvenly,
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier
//                            .padding(top = 24.dp)
//                            .fillMaxWidth()
//                            .height(550.dp)
//                            .animateContentSize()
//                    ) {
//                        items(state.video.screenshots) { screenshot ->
//                            val file = File(screenshot.path)
//                            val interactionSource = MutableInteractionSource()
//                            val isHovered by interactionSource.collectIsHoveredAsState()
//
//                            Surface(
//                                shape = RoundedCornerShape(10.dp),
//                                elevation = 0.dp,
//                                modifier = Modifier
//                                    .width(250.dp)
//                                    .fillMaxHeight(.9f)
//                                    .weight(1f)
//                                    .hoverable(interactionSource)
//                            ) {
//                                Box(
//                                    modifier = Modifier.fillMaxSize()
//                                ) {
//                                    ImageFromBufferedImage(
//                                        file = file,
//                                        onError = { screenshotPath ->
//                                            vm.trySend(
//                                                VideoScreenshotsContract.Inputs.OnScreenshotNotExist(
//                                                    screenshotPath
//                                                )
//                                            )
//                                        },
//                                        modifier = Modifier.fillMaxSize()
//                                    )
//                                    androidx.compose.animation.AnimatedVisibility(
//                                        visible = isHovered,
//                                        enter = fadeIn(),
//                                        exit = fadeOut(),
//                                        modifier = Modifier
//                                            .padding(bottom = 8.dp)
//                                            .align(Alignment.BottomCenter)
//                                    ) {
//                                        Surface(
//                                            shape = CircleShape,
//                                            elevation = 0.dp,
//                                            color = MaterialTheme.colors.secondary,
//                                            modifier = Modifier
//                                                .size(50.dp)
//                                                .clickable {
//                                                    vm.trySend(
//                                                        VideoScreenshotsContract.Inputs.DeleteScreenshot(
//                                                            screenshotId = screenshot.id
//                                                        )
//                                                    )
//                                                }
//                                        ) {
//                                            Box(
//                                                contentAlignment = Alignment.Center,
//                                                modifier = Modifier.fillMaxSize()
//                                            ) {
//                                                Icon(
//                                                    imageVector = Icons.Default.Clear,
//                                                    contentDescription = "Delete screenshot",
//                                                    tint = MaterialTheme.colors.onSecondary,
//                                                    modifier = Modifier
//                                                        .fillMaxSize(.5f)
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
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
