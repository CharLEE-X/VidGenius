package com.charleex.vidgenius.datasource.di

import org.koin.core.module.Module
import java.io.File

internal expect fun platformModule(appDataDir: File): Module
