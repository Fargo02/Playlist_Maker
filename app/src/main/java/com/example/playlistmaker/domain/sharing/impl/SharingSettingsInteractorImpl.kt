package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.settings.SharingInteractor
import com.example.playlistmaker.domain.sharing.settings.ExternalNavigator

class SharingSettingsInteractorImpl(
    private val externalNavigator: ExternalNavigator
): SharingInteractor {
    override fun share(message : String) {
        externalNavigator.share(message)
    }

    override fun openTerms(link : String) {
        externalNavigator.openLink(link)
    }

    override fun openSupport(email : EmailData) {
        externalNavigator.openEmail(email)
    }
}