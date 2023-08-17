plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
}

group = "com.charleex.vidgenius.video_screenshots"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)

                implementation(libs.java.javacv)
                implementation(libs.java.ffmpeg)
                implementation("com.google.guava:guava:32.1.1-jre")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.test.mockative.core)
            }
        }
    }
}

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, libs.test.mockative.processor)
        }
}

ksp {
    arg("mockative.stubsUnitByDefault", "true")
}
