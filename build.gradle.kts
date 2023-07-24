// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    kotlin("jvm") version libs.versions.kotlin.get() apply false
    kotlin("multiplatform") version libs.versions.kotlin.get() apply false
    kotlin("plugin.serialization") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.compose") version libs.versions.compose.get() apply false
}
