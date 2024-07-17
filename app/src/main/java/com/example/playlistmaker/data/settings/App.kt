package com.example.playlistmaker.data.settings

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val KEY_NIGHT_MODE = "nightMode"

class App : Application() {
    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val isDarkThemeOn = getSharedPreferences(KEY_NIGHT_MODE, MODE_PRIVATE)

        darkTheme = isDarkThemeOn.getBoolean(KEY_NIGHT_MODE, false)
        switchTheme(darkTheme)
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