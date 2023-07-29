plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.charleex.vidgenius.feature.root"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ballast.core)
                api(libs.ballast.navigation)
                implementation(libs.koin.core)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
    }
}
