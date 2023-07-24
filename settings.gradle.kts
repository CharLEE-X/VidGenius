pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "AutoYtVid"

include(
    ":app",
    ":ui",
    ":yt",
    ":ai",
    ":api",
    ":repository",
    ":feature:root",
    ":feature:router",
    ":feature:video-list",
    ":feature:video-detail",
    ":feature:drag-drop",
)
