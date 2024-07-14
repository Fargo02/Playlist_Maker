package com.example.playlistmaker.ui.settings.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.playlistmaker.data.settings.App
import com.example.playlistmaker.utils.Creator

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    private val changeThemeEvent = SingleLiveEvent<Boolean>()
    val sharingSettingsInteractor = Creator.provideSharingSettingsInteractor(application)
    init {
        (
                changeThemeEvent.postValue((application as App).darkTheme)
        )
    }

    fun observeChangeTheme(): LiveData<Boolean> = changeThemeEvent

}