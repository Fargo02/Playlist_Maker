package com.example.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

const val KEY_NIGHT_MODE = "nightMode"

class Application: Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidContext(this@Application)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

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