package com.example.weatherdemo.util

import android.content.SearchRecentSuggestionsProvider
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class HistorySearchSuggestionsProvider :SearchRecentSuggestionsProvider() {

    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {

        const val AUTHORITY = "com.example.weatherdemo.util.HistorySearchSuggestionsProvider"
        const val MODE = DATABASE_MODE_QUERIES
    }
}