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
//                implementation(libs.google.api)
//                implementation(libs.google.oauth)
//                implementation(libs.google.youtube.services)
//                implementation(libs.google.youtube.analytics)

                val yt = "v3-rev20230521-2.0.0"
                val jetty = "1.34.0"
                val client = "2.2.0"
                val jackson = "1.43.3"

//                implementation(enforcedPlatform("com.google.cloud:libraries-bom:26.20.0"))
                implementation("com.google.http-client:google-http-client-jackson2:$jackson")

                implementation("com.google.api-client:google-api-client:$client")
                implementation("com.google.oauth-client:google-oauth-client-jetty:$jetty")
                implementation("com.google.apis:google-api-services-youtube:$yt")
//                implementation("com.google.apis:google-api-services-youtubeAnalytics")
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
