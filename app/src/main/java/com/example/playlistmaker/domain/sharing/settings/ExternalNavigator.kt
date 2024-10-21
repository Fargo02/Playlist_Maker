package com.example.playlistmaker.domain.sharing.settings

import com.example.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {

    fun share(message : String)

    fun openLink(link : String)

    fun openEmail(emailData : EmailData)

}