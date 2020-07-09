package com.example.weatherdemo.ui.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.joda.time.DateTime

object LoadDayOfWeekAdapter {

    @JvmStatic
    @BindingAdapter("dayOfWeek")
    fun convertDay(textView: TextView, dt: Long) {
        val dateTime = DateTime(dt*1000)
        textView.text = dateTime.dayOfWeek().asText
    }
}