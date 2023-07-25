package com.charleex.vidgenius.settings

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import java.util.prefs.Preferences

val settingsModule = module {
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
