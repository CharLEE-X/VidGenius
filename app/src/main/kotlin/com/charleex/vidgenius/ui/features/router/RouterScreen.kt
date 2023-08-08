package com.charleex.vidgenius.ui.features.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

private const val DASHBOARD = "/app"
private const val GENERATION = "/generation"

enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Dashboard(routeFormat = DASHBOARD),
    Generation(routeFormat = DASHBOARD + GENERATION),
    ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}
