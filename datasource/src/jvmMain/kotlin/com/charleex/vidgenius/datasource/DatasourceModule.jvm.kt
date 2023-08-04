package com.charleex.vidgenius.datasource

import com.charleex.vidgenius.datasource.db.VidGeniusDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.asJdbcDriver
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

private const val DATABASE_NAME = "vid_genius.db"
private const val DATABASE_DIR = "database"

internal actual fun platformModule(appDataDir: File): Module = module {
    single<SqlDriver> {
        val dataSource = createHikariDataSource(appDataDir)
        val driver = dataSource.asJdbcDriver()
        VidGeniusDatabase.Schema.create(driver)
        driver
    }
}

private fun createHikariDataSource(appDataDir: File): HikariDataSource {
    val databaseDir = File(appDataDir, DATABASE_DIR)
    if (!databaseDir.exists()) {
        databaseDir.mkdirs()
    }
    val databaseFilePath = databaseDir.resolve(DATABASE_NAME).absolutePath
    val config = HikariConfig()
    config.jdbcUrl = "jdbc:sqlite:$databaseFilePath"
    config.driverClassName = "org.sqlite.JDBC"
    return HikariDataSource(config)
}

private fun getDatabaseFile(appDataDir: File): File {
    val databaseDir = File(appDataDir, DATABASE_DIR)
    if (!databaseDir.exists()) {
        databaseDir.mkdirs()
    }
    return databaseDir.resolve(DATABASE_NAME)
}
