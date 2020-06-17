package com.example.weatherdemo.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherdemo.data.model.*
import com.example.weatherdemo.util.WeatherInfoListDataConverter
import com.example.weatherdemo.util.WeatherListDataConverter

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weatherResponse: WeatherResponse)

    @Query("select * from weather_table where weatherid = 0")
    fun loadWeather(): LiveData<WeatherResponse>
}

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSearchItem(searchHistoryItem: SearchHistoryItem)

    @Query("select * from item_table")
    fun loadSearchHistory(): LiveData<List<SearchHistoryItem>>

    @Query("update item_table set checked = :checked where name = :city")
    fun updateSearchItem(city: String, checked: Boolean)

    @Query("update item_table set checked = :checked")
    fun updateSearchItemUnchecked(checked: Boolean)

    @Query("delete from item_table where name = :city")
    fun deleteCityByName(city: String)

    @Query("delete from item_table where checked = :checked")
    fun deleteCheckedCity(checked: Boolean)

    @Query("delete from item_table")
    fun deleteall()
}

@Database(entities = [WeatherResponse::class, WeatherInfo::class, City::class, Coord::class, SearchHistoryItem::class], version = 1, exportSchema = false)
@TypeConverters(WeatherInfoListDataConverter::class, WeatherListDataConverter::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
    abstract val searchDao: SearchDao
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