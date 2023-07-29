enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "VidGenius"

include(
    ":app",
    ":ui",
)
include(
    ":api",
    ":datasource",
    ":ai",
    ":youtube",
    ":video-processor",
    ":vision-ai",
)

include(
    ":feature:root",
    ":feature:router",
    ":feature:video-list",
    ":feature:video-detail",
    ":feature:process-video",
)
