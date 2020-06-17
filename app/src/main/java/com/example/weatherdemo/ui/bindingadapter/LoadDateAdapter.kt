package com.example.weatherdemo.ui.bindingadapter

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weatherdemo.R
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import timber.log.Timber

class LoadDateAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("dateOfWeek")
        fun convertDate(textView: TextView, dt: Long) {
            val dateTime = DateTime(dt*1000, DateTimeZone.UTC)
            textView.text = "${dateTime.dayOfMonth} ${dateTime.monthOfYear().asText}"
        }
    }
}