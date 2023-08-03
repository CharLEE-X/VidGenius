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
    ":video-processor",
    ":vision-ai",
    ":youtube",
)

include(
    ":feature:root",
    ":feature:router",
    ":feature:video-list",
    ":feature:video-detail",
    ":feature:process-videos",
)
