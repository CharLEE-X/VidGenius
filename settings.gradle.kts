enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "VidGenius"

include(
    ":app",
    ":ui",
)
include(
    ":yt",
    ":ai",
    ":api",
    ":video-processor",
    ":datasource",
)

include(
    ":feature:root",
    ":feature:router",
    ":feature:video-list",
    ":feature:video-detail",
    ":feature:drag-drop",
    ":feature:video-screenshots",
    ":feature:screenshots-description",
)
