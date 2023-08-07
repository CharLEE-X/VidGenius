package com.charleex.vidgenius.ui.features.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

private const val DASHBOARD = "/app"
private const val SETTINGS = "/settings"

private const val ANIMALS_UPLOADS = "/animals-uploads"
private const val ANIMALS_GENERATION = "/animals-generation"
private const val ANIMALS_SUBTITLES = "/animals-subtitles"

private const val FAILS_UPLOADS = "/fails-uploads"
private const val FAILS_GENERATION = "/fails-generation"
private const val FAILS_SUBTITLES = "/fails-subtitles"

enum class RouterScreen(
    routeFormat: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Dashboard(routeFormat = DASHBOARD),
    Settings(routeFormat = DASHBOARD + SETTINGS),

    AnimalsUploads(routeFormat = DASHBOARD + ANIMALS_UPLOADS),
    AnimalsGeneration(routeFormat = DASHBOARD + ANIMALS_GENERATION),
    AnimalsSubtitles(routeFormat = DASHBOARD + ANIMALS_SUBTITLES),

    FailsUploads(routeFormat = DASHBOARD + FAILS_UPLOADS),
    FailsGeneration(routeFormat = DASHBOARD + FAILS_GENERATION),
    FailsSubtitles(routeFormat = DASHBOARD + FAILS_SUBTITLES),
    ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}
