package com.example.playlistmaker.ui.create_playlist.frgament

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.create_playlist.view_model.CreatePlaylistViewModel
import com.example.playlistmaker.utils.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.playlistmaker.utils.showSnackbar

class FragmentCreatePlaylist(): BindingFragment<FragmentCreatePlaylistBinding>() {

    private var namePlaylist = ""
    private var descriptionPlaylist = ""
    private var coverPlaylist = ""
    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel: CreatePlaylistViewModel by viewModel()

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreatePlaylistBinding {
        return FragmentCreatePlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        confirmDialog  = MaterialAlertDialogBuilder(requireContext(), R.style.dialogView)
            .setTitle(R.string.close_create_playlist)
            .setMessage(R.string.all_data_will_be_lost)
            .setPositiveButton(R.string.сomplete) { _, _ ->
                findNavController().popBackStack()
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                Log.i("clickToCancel", "cancel")
            }

        binding.bottomCreate.isEnabled = false

        binding.toolbar.setNavigationOnClickListener {
            showConfirmDialog()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    coverPlaylist = uri.toString()
                    binding.imageCoverPlaceholder.isVisible = false
                    Glide.with(binding.cover)
                        .load(uri)
                        .transform(
                            CenterCrop(), RoundedCorners(8)
                        )
                        .into(binding.imageCover)
                } else {
                    coverPlaylist = ""
                    binding.imageCoverPlaceholder.isVisible = true
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val nameTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                namePlaylist = s.toString()
                if (namePlaylist != "") {
                    binding.bottomCreate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue)))
                    binding.bottomCreate.isEnabled = true
                } else {
                    binding.bottomCreate.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey)))
                    binding.bottomCreate.isEnabled = false
                }
            }
            override fun afterTextChanged(s: Editable?) { }
        }
        nameTextWatcher.let { binding.inputEditTextName.addTextChangedListener(it) }

        val descriptionTextWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                descriptionPlaylist = s.toString()
            }

            override fun afterTextChanged(s: Editable?) { }
        }
        descriptionTextWatcher.let { binding.inputEditTextDescription.addTextChangedListener(it) }

        binding.bottomCreate.setOnClickListener {
            viewModel.addPlaylist(Playlist(
                id = 0,
                name = namePlaylist,
                description = descriptionPlaylist,
                imageUri = coverPlaylist,
            ))
            showSnackbar(requireView(), requireContext(), namePlaylist, R.string.playlist_created)

            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmDialog()
            }
        })
    }

    private fun showConfirmDialog() {
        if (namePlaylist != "" || descriptionPlaylist != "" || coverPlaylist != "") {
            confirmDialog.show()
        } else {
            findNavController().popBackStack()
        }
    }
}