package com.example.weatherdemo.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item_table")
data class SearchHistoryItem (
    @PrimaryKey
    val name: String,
    var checked: Boolean = false
)