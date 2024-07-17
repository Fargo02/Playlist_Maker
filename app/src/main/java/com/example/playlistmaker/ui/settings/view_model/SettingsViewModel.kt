package com.example.playlistmaker.ui.settings.view_model

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.data.settings.App
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.SharingSettingsInteractor
import com.example.playlistmaker.utils.Creator

class SettingsViewModel(
    private val sharingSettingsInteractor: SharingSettingsInteractor,
    context: Context
): ViewModel() {
    private val changeThemeEvent = SingleLiveEvent<Boolean>()
    companion object {
        fun factory(context : Context) = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSharingSettingsInteractor(context),
                    context
                )
            }
        }
    }

    init {
        (
                changeThemeEvent.postValue((context as App).darkTheme)
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