package com.example.playlistmaker.domain.sharing.settings

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingSettingsInteractor {

    fun shareApp(link : String)

    fun openTerms(link : String)

    fun openSupport(email : EmailData)

}