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

enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    FeatureList(ROOT + FEATURE_LIST),
    Login(ROOT + LOGIN),
    VideoList(ROOT + VIDEO_LIST),
    VideoDetail(ROOT + VIDEO_DETAIL),
    DragDrop(ROOT + DRAG_AND_DROP)
    ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}


fun RouterScreen.label() = when (this) {
    RouterScreen.FeatureList -> "Feature List"
    RouterScreen.Login -> "Login"
    RouterScreen.VideoList -> "Video List"
    RouterScreen.VideoDetail -> "Video Detail ${this.matcher.path.last()} "
    RouterScreen.DragDrop -> "Drag and Drop"
}
