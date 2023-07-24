plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.hackathon.cda.repository"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.yt)
                implementation(projects.ai)
                implementation(projects.api)
                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.uuid)
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
