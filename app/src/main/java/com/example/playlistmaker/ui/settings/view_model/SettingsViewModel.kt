package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor
import com.example.playlistmaker.utils.Application

class SettingsViewModel(
    private val sharingSettingsInteractor: SharingSettingsInteractor,
    context: Context
): ViewModel() {

    private val changeThemeEvent = SingleLiveEvent<Boolean>()

    init {
        (
                changeThemeEvent.postValue((context as Application).darkTheme)
        )
    }

    fun observeChangeTheme(): LiveData<Boolean> = changeThemeEvent

    fun getShareTheApp(link: String) {
        sharingSettingsInteractor.shareApp(link)
    }

    fun getWriteToSupport(email: EmailData) {
        sharingSettingsInteractor.openSupport(email)
    }

    fun getUserAgreement(link: String) {
        sharingSettingsInteractor.openTerms(link)
    }

}