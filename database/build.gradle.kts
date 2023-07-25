plugins {
    kotlin("multiplatform")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization")
}

group = "com.charleex.autovidyt.database"
version = "1.0-SNAPSHOT"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.koin.core)
                implementation(libs.kotlin.coroutines)
                implementation(libs.sqlDelight.coroutinesExtensions)
                implementation(libs.sqlDelight.runtime)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.sqlDelight.driverJVM)
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
