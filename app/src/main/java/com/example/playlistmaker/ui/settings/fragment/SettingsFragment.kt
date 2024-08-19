package com.example.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import com.example.playlistmaker.utils.Application
import com.example.playlistmaker.utils.BindingFragment
import com.example.playlistmaker.utils.KEY_NIGHT_MODE
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment(): BindingFragment<FragmentSettingsBinding>() {

    private val viewModel: SettingsViewModel by viewModel()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater,container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences(KEY_NIGHT_MODE, MODE_PRIVATE)

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked, sharedPreferences)
        }

        viewModel.observeChangeTheme().observe(viewLifecycleOwner) {
            binding.themeSwitcher.isChecked = it
            Log.i("theme", "$it")
        }

        binding.shareTheApp.setOnClickListener {
            viewModel.getShareTheApp(getShareAppLink())
        }

        binding.writeToSupport.setOnClickListener {
            viewModel.getWriteToSupport(getSupportEmailData())

        }

        binding.userAgreement.setOnClickListener {
            viewModel.getUserAgreement(getTermsLink())
        }
    }
    private fun getShareAppLink(): String {
        return getString(R.string.link_to_the_practicum)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(
            address = getString(R.string.mail),
            subject = getString(R.string.title_message_for_developer),
            text = getString(R.string.message_for_developer)
        )
    }

    private fun getTermsLink(): String {
        return getString(R.string.link_to_the_agreement)
    }
}