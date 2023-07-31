plugins {
    kotlin("multiplatform")
}

group = "com.charleex.vidgenius.feature.process-videos"
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