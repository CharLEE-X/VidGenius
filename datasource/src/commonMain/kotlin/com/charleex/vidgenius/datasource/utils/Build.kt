package com.charleex.vidgenius.datasource.utils

import java.io.FileReader
import java.util.Properties

fun getIsDebugBuild(): Boolean {
    val properties = Properties()
    properties.load(FileReader("/Users/adrianwitaszak/CharLEEX/VidGenius/local.properties"))

    val isDebugBuildString = properties.getProperty("isDebugBuild")
    return isDebugBuildString?.toBoolean() ?: false
}
