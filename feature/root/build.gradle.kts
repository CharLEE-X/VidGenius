plugins {
    kotlin("multiplatform")
}

group = "com.charleex.vidgenius.feature.root"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.datasource)
                implementation(projects.feature.router)

                implementation(libs.ballast.core)
                implementation(libs.ballast.savedState)
                implementation(libs.koin.core)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}
