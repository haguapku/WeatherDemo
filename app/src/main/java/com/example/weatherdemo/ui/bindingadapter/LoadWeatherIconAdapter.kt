package com.example.weatherdemo.ui.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class LoadWeatherIconAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("weatherIcon")
        fun loadWeatherIcon(imageView: ImageView, imageUrl: String) {
            Glide.with(imageView.context.applicationContext).load(imageUrl).into(imageView)
        }
    }
}