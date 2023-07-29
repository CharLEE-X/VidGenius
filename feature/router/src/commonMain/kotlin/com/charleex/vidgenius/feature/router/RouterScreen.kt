package com.charleex.vidgenius.feature.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher
import kotlinx.serialization.Serializable

private const val ROOT = "/root"
private const val FEATURE_LIST = "/feature-list"
private const val LOGIN = "/login"
private const val VIDEO_LIST = "/videos"
private const val VIDEO_DETAIL = "/videos/{videoId}"
private const val PROCESS_VIDEO = "/process-video"
private const val VIDEO_SCREENSHOTS = "/video-screenshots/{videoId}"
private const val SCREENSHOTS_TO_TEXT = "/screenshots-to-text/{videoId}"

@Serializable
enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    FeatureList(ROOT + FEATURE_LIST),
    Login(ROOT + LOGIN),
    VideoList(ROOT + VIDEO_LIST),
    VideoDetail(ROOT + VIDEO_DETAIL),
    ProcessVideo(ROOT + PROCESS_VIDEO),
    VideoScreenshots(ROOT + VIDEO_SCREENSHOTS),
    ScreenshotsToText(ROOT + SCREENSHOTS_TO_TEXT)
    ;

    override val matcher: RouteMatcher by lazy { RouteMatcher.create(routeFormat) }
}

fun RouterScreen?.label(): String = when (this) {
    RouterScreen.FeatureList -> ""
    RouterScreen.Login -> "Login"
    RouterScreen.VideoList -> "Video List"
    RouterScreen.VideoDetail -> "Video Detail"
    RouterScreen.ProcessVideo -> "Drag Drop"
    RouterScreen.VideoScreenshots -> "Video Screenshots"
    RouterScreen.ScreenshotsToText -> "Screenshots To Text"
    null -> ""
}
