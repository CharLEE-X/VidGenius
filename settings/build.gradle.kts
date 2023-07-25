plugins {
    kotlin("multiplatform")
}

group = "com.charleex.autovidyt.settings"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlin.coroutines)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.multiplatformSettings.serialization)
            }
        }
    }
}
