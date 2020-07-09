package com.example.weatherdemo.ui.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object ConvertFloatToIntAdapter {

    @JvmStatic
    @BindingAdapter("tempText")
    fun convertTemp(textView: TextView, temp: Float) {
        textView.text = "${temp.toInt()}\u00B0"
    }
}