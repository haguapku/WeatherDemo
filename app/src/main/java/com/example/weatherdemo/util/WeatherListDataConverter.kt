package com.example.weatherdemo.util

import androidx.room.TypeConverter
import com.example.weatherdemo.data.model.Weather
import java.util.Collections.emptyList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class WeatherListDataConverter {

    @TypeConverter
    fun stringToWeatherList(data: String?): List<Weather> {

        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<Weather>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun WeatherListToString(someObjects: List<Weather>): String {
        return Gson().toJson(someObjects)
    }
}