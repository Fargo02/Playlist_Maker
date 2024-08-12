package com.example.playlistmaker.ui.library.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.ui.library.view_model.FavouriteTracksViewModel
import com.example.playlistmaker.utils.BindingFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteTracksFragment(): BindingFragment<FragmentFavouritesBinding>() {
    companion object {
        fun newInstance() = FavouriteTracksFragment()
    }

    private val posterViewModel: FavouriteTracksViewModel by viewModel()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentFavouritesBinding {
        return FragmentFavouritesBinding.inflate(inflater, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeholderGroup.isVisible = true
    }
}