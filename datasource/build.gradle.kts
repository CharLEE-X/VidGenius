plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight")
}

group = "com.hackathon.cda.repository"

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.ai)
                implementation(projects.api)
                implementation(projects.videoProcessor)
                implementation(projects.visionAi)
                implementation(projects.youtube)

                implementation(libs.koin.core)
                implementation(libs.kotlin.dateTime)
                implementation(libs.kotlin.coroutines)
                implementation(libs.kotlin.kermit)
                implementation(libs.kotlin.serialization.core)
                implementation(libs.kotlin.serialization.json)
                implementation(libs.kotlin.uuid)
                implementation(libs.sqlDelight.coroutinesExtensions)
                implementation(libs.sqlDelight.runtime)
                implementation(libs.multiplatformSettings.core)
                implementation(libs.multiplatformSettings.coroutines)
                implementation(libs.multiplatformSettings.serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.koin.test)
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

