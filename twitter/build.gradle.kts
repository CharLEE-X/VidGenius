plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
}

group = "com.charleex.vidgenius.twitter"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.openAi)
                implementation(projects.visionAi)

                implementation(libs.koin.core)

                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.uuid)

                implementation("org.twitter4j:twitter4j-core:4.0.7")
                implementation("io.github.takke:jp.takke.twitter4j-v2:1.4.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.test.mockative.core)
                implementation(libs.test.kotlin.turbine)
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
