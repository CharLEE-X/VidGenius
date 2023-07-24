package com.charleex.autoytvid.feature.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

private const val ROOT = "/root"
private const val FEATURE_LIST = "/feature-list"
private const val LOGIN = "/login"
private const val VIDEO_LIST = "/videos"
private const val VIDEO_DETAIL = "/videos/{id}"
private const val DRAG_AND_DROP = "/drag-and-drop"
private const val VIDEO_SCREENSHOTS = "/video-screenshots/{path}"

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

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}
