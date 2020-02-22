package com.example.weatherdemo.util

import androidx.room.TypeConverter
import com.example.weatherdemo.data.model.WeatherInfo
import java.util.Collections.emptyList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ListDataConverter {

    @TypeConverter
    fun stringToWeatherInfoList(data: String?): List<WeatherInfo> {

        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<WeatherInfo>>() {}.type

        return Gson().fromJson(data, listType)
    }

    @TypeConverter
    fun WeatherInfoListToString(someObjects: List<WeatherInfo>): String {
        return Gson().toJson(someObjects)
    }
}