package com.example.playlistmaker.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.playlistmaker.R
import com.google.android.material.snackbar.Snackbar


fun showSnackbar(view: View, context: Context, message: String, resourceId: Int) {
    val snackBar = Snackbar.make(view, context.getString(resourceId, message), Snackbar.LENGTH_LONG)
    snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.custom_color_on_background))
    val textView = snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
    textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
    textView.gravity = Gravity.CENTER
    textView.setTextColor(ContextCompat.getColor(context, R.color.custom_color_on_text))
    snackBar.show()
}