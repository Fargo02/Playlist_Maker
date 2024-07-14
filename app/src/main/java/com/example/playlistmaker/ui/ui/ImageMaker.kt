package com.example.playlistmaker.ui.ui

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class ImageMaker {
    fun getPhoto(imageView: ImageView, url: String, placeholder: Int, radius: Int) {
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeholder)
            .fitCenter()
            .transform(RoundedCorners(radius))
            .into(imageView)
    }
}