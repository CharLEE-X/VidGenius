plugins {
    kotlin("multiplatform")
}

group = "com.charleex.autovidyt.feature.video-list"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.datasource)

                implementation(libs.ballast.core)
                implementation(libs.ballast.savedState)
                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
            }
        }
    }
}
