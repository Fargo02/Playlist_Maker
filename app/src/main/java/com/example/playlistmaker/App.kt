package com.example.playlistmaker

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

const val KEY_NIGHT_MODE = "nightMode"
class App : Application() {

    var darkTheme = true
    override fun onCreate() {
        super.onCreate()
        //val isDarkThemeOn = resources.configuration.isNightModeActive
        val isDarkThemeOn = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES
        darkTheme = getSharedPreferences(KEY_NIGHT_MODE, MODE_PRIVATE)
            .getBoolean(KEY_NIGHT_MODE, isDarkThemeOn)
    }
    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}