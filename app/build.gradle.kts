import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.charleex.vidgenius"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(projects.datasource)

    implementation(compose.desktop.currentOs)
    implementation(compose.ui)
    implementation(compose.runtime)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(compose.animationGraphics)
    implementation(compose.animation)
    implementation(compose.preview)
    implementation(libs.jetbrains.compose.ui.util)

    implementation(libs.ballast.core)
    implementation(libs.ballast.navigation)

    implementation(libs.koin.core)
    implementation(libs.kotlin.dateTime)
    implementation(libs.ktor.io.core)
    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.2")
    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.2.1")
    implementation("uk.co.caprica:vlcj:4.8.2")
    implementation("com.github.ltttttttttttt:load-the-image:1.0.8")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "VidGenius"
            packageVersion = "1.0.0"
            copyright = "©2023 VidGenius by Adrian Witaszak. All rights reserved."

            macOS {
                iconFile.set(project.file("icon.icns"))
            }
        }
    }
}
