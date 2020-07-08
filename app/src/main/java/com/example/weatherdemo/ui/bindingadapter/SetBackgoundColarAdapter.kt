package com.example.weatherdemo.ui.bindingadapter

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weatherdemo.R
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import timber.log.Timber

class SetBackgoundColarAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("backgroundColor")
        fun setBackgroundColor(constraintLayout: ConstraintLayout, checked: Boolean) {
            if (checked) {
                constraintLayout.setBackgroundColor(Color.GRAY)
            } else {
                constraintLayout.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}