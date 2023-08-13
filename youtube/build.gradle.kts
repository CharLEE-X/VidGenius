plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
}

group = "com.charleex.vidgenius.youtube"

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

                // Youtube
                val yt = "v3-rev20230521-2.0.0"
                val jetty = "1.34.0"
                val client = "2.2.0"
                val jackson = "1.43.3"
                implementation("com.google.http-client:google-http-client-jackson2:$jackson")
                implementation("com.google.api-client:google-api-client:$client")
                implementation("com.google.oauth-client:google-oauth-client-jetty:$jetty")
                implementation("com.google.apis:google-api-services-youtube:$yt")
                implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")
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
