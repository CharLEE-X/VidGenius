package com.charleex.vidgenius.datasource.di

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import com.charleex.vidgenius.datasource.settings.SettingKeys
import java.util.prefs.Preferences

internal val settingsModule = module {
    single {
        SettingKeys
    }
    single<Preferences> {
        Preferences.userRoot().node(SettingKeys.USER_DATA_PREFS)
    }
    single<Settings> {
        val delegate: Preferences = get<Preferences>()
        PreferencesSettings(delegate)
    }
    single<ObservableSettings> {
        val delegate: Preferences = get<Preferences>()
        PreferencesSettings(delegate)
    }
}
