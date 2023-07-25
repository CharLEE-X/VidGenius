package com.charleex.vidgenius.feature.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher
import kotlinx.serialization.Serializable

private const val ROOT = "/root"
private const val FEATURE_LIST = "/feature-list"
private const val LOGIN = "/login"
private const val VIDEO_LIST = "/videos"
private const val VIDEO_DETAIL = "/videos/{id}"
private const val DRAG_AND_DROP = "/drag-and-drop"
private const val VIDEO_SCREENSHOTS = "/video-screenshots/{path}"

@Serializable
enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    FeatureList(ROOT + FEATURE_LIST),
    Login(ROOT + LOGIN),
    VideoList(ROOT + VIDEO_LIST),
    VideoDetail(ROOT + VIDEO_DETAIL),
    DragDrop(ROOT + DRAG_AND_DROP),
    VideoScreenshots(ROOT + VIDEO_SCREENSHOTS)
    ;

    override val matcher: RouteMatcher by lazy { RouteMatcher.create(routeFormat) }
}

fun RouterScreen?.label(): String = when (this) {
    RouterScreen.FeatureList -> ""
    RouterScreen.Login -> "Login"
    RouterScreen.VideoList -> "Video List"
    RouterScreen.VideoDetail -> "Video Detail"
    RouterScreen.DragDrop -> "Drag Drop"
    RouterScreen.VideoScreenshots -> "Video Screenshots"
    null -> ""
}
