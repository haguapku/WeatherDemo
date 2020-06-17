package com.example.weatherdemo.ui.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weatherdemo.R
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

class LoadDayOfWeekAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("dayOfWeek")
        fun convertDay(textView: TextView, dt: Long) {
            val dateTime = DateTime(dt*1000)
            textView.text = dateTime.dayOfWeek().asText
        }
    }
}