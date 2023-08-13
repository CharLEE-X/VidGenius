enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "VidGenius"

include(
    ":app",
    ":datasource",
    ":open-ai",
    ":vision-ai",
    ":youtube",
    ":video-screenshots",
)
