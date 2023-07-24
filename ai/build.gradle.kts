plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.hackathon.cda.whisper.ai"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.api)
                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.okhttp)
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
