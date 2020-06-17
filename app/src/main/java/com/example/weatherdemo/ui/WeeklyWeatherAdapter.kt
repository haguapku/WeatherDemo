package com.example.weatherdemo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.R
import com.example.weatherdemo.data.model.WeatherInfo

class WeeklyWeatherAdapter(var weatherInfoList: MutableList<WeatherInfo>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun setWeatherInfos(newWeatherInfoList: List<WeatherInfo>){
        weatherInfoList.clear()
        weatherInfoList.addAll(newWeatherInfoList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WeatherViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_weekly_weather, parent, false))
    }

    override fun getItemCount(): Int {
        return weatherInfoList.size - 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WeatherViewHolder -> {
                holder.bind(weatherInfoList[position+1])
            }
        }
    }
}