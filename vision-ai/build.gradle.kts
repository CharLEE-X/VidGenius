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
//                <groupId>com.google.http-client</groupId>
//                <artifactId>google-http-client-jackson2</artifactId>
//                <version>1.40.1</version>
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
