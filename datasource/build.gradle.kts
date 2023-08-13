plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlinx.kover") version "0.7.3"
}

group = "com.charleex.vidgenius.datasource"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.openAi)
                implementation(projects.visionAi)
                implementation(projects.youtube)
                implementation(projects.videoScreenshots)

                implementation(libs.koin.core)

                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.uuid)

                implementation(libs.ktor.client.okhttp)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.okhttp)

                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.multiplatformSettings.serialization)

                implementation(libs.sqlDelight.coroutinesExtensions)
                implementation(libs.sqlDelight.runtime)

                // Video
                implementation(libs.java.javacv)
                implementation(libs.java.ffmpeg)

                // Google cloud
                implementation(enforcedPlatform("com.google.cloud:libraries-bom:26.20.0"))
                implementation("com.google.cloud:google-cloud-vision")
                implementation("com.google.guava:guava:32.1.1-jre")
                implementation("com.google.http-client:google-http-client-jackson2")

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
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.sqlDelight.driverJVM)
                implementation("app.cash.sqldelight:jdbc-driver:2.0.0-rc02")
                implementation("com.zaxxer:HikariCP:5.0.0")
            }
        }
    }
}

sqldelight {
    database("VidGeniusDatabase") {
        packageName = "${project.group}.db"
        schemaOutputDirectory = File("src/commonMain/sqldelight")
        dialect = "sqlite:3.24"
        verifyMigrations = true
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
