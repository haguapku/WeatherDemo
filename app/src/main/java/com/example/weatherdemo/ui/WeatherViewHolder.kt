package com.example.weatherdemo.ui

import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.data.model.WeatherInfo
import com.example.weatherdemo.databinding.ItemWeeklyWeatherBinding

class WeatherViewHolder(private val itemWeeklyWeatherBinding: ItemWeeklyWeatherBinding): RecyclerView.ViewHolder(itemWeeklyWeatherBinding.root) {

    fun bind(item: WeatherInfo) {
        itemWeeklyWeatherBinding.weatherInfo = item
        itemWeeklyWeatherBinding.executePendingBindings()
    }
}