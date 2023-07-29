plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

group = "com.hackathon.cda.screenshot-to-text"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(enforcedPlatform("com.google.cloud:libraries-bom:26.19.0"))
                implementation("com.google.cloud:google-cloud-vision")
                implementation("com.google.guava:guava:32.1.1-jre")
                implementation("com.google.http-client:google-http-client-jackson2")

                implementation(kotlin("reflect"))
                implementation("com.google.cloud:google-cloud-vision:3.20.0")
//                // see: https://github.com/googleapis/sdk-platform-java/pull/1832
//                modules {
//                    module("com.google.guava:listenablefuture") {
//                        replacedBy("com.google.guava:guava", "listenablefuture is part of guava")
//                    }
//                }
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
