package com.charleex.vidgenius.ui.features.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

private const val DASHBOARD = "/app"
private const val VIDEOS = "/videos"
private const val VIDEO_DETAIL = "/video/{videoId}"

enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Dashboard(routeFormat = DASHBOARD),
    Videos(routeFormat = DASHBOARD + VIDEOS),
    VideoDetail(routeFormat = DASHBOARD + VIDEO_DETAIL),
    ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}
