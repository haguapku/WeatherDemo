package com.example.weatherdemo.ui.bindingadapter

import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter

object SetBackgroundColorAdapter {

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