package com.example.playlistmaker.presentation.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PhotoMaker {
    fun getPhoto(imageView: ImageView, url: String, placeholder: Int, radius: Int) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(imageView)
    }
}