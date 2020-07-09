package com.example.weatherdemo.ui.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

object LoadDateAdapter {

    @JvmStatic
    @BindingAdapter("dateOfWeek")
    fun convertDate(textView: TextView, dt: Long) {
        val dateTime = DateTime(dt*1000, DateTimeZone.UTC)
        textView.text = "${dateTime.dayOfMonth} ${dateTime.monthOfYear().asText}"
    }
}