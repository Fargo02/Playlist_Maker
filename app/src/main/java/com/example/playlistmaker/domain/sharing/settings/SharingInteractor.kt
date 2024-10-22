package com.example.playlistmaker.domain.sharing.settings

import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {

    fun share(message : String)

    fun openTerms(link : String)

    fun openSupport(email : EmailData)

}