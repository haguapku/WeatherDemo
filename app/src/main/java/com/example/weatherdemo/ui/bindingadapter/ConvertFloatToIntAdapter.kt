package com.example.weatherdemo.ui.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class ConvertFloatToIntAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("tempText")
        fun convertTemp(textView: TextView, temp: Float) {
            textView.text = "${temp.toInt()}\u00B0"
        }
    }
}