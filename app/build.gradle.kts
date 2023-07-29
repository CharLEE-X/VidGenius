import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.charleex.vidgenius"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(projects.ui)

    implementation(compose.desktop.currentOs)
    implementation(libs.koin.core)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AutoYtVid"
            packageVersion = "1.0.0"
        }
    }
}
