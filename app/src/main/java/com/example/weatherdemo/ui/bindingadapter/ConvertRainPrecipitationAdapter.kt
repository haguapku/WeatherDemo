package com.example.weatherdemo.ui.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class ConvertRainPrecipitationAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("rainPrecipitation")
        fun convertRainPrecipitation(textView: TextView, rain: Float) {
            when {
                rain < 1 -> textView.text = "<1mm"
                rain < 5 -> textView.text = "1-5mm"
                rain < 10 -> textView.text = "1-10mm"
                rain < 20 -> textView.text = "10-20mm"
                else -> textView.text = ">20mm"
            }
        }
    }
}