package com.example.weatherdemo.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherdemo.data.model.*
import com.example.weatherdemo.util.ListDataConverter

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weatherResponse: WeatherResponse)

    @Query("select * from weather_table where weatherid = 0")
    fun loadWeather(): LiveData<WeatherResponse>
}

@Database(entities = [WeatherResponse::class, WeatherInfo::class, MainData::class, Clouds::class, Wind::class, Sys::class, City::class, Coord::class], version = 1, exportSchema = false)
@TypeConverters(ListDataConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}

private lateinit var INSTANCE: WeatherDatabase

fun getDatabase(context: Context): WeatherDatabase {
    synchronized(WeatherDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_db"
                )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}