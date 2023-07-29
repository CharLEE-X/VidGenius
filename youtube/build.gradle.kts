plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.charleex.vidgenius.youtube"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.uuid)
                implementation(libs.google.api)
                implementation(libs.google.oauth)
                implementation(libs.google.youtube.services)
                implementation(libs.google.youtube.analytics)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.koin.test)
            }
        }
    }
}
