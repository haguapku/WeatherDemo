package com.example.weatherdemo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.db.SearchDao
import com.example.weatherdemo.util.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    private val searchDao: SearchDao,
    private val contextProvider: CoroutineContextProvider
): ViewModel() {

    val searchHistoryLivaData: LiveData<List<SearchHistoryItem>> by lazy(LazyThreadSafetyMode.NONE) {
        searchDao.loadSearchHistory()
    }

    fun insert(searchHistoryItem: SearchHistoryItem) = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.insertSearchItem(searchHistoryItem)
        }
    }

    fun update(name: String, checked: Boolean) = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.updateSearchItem(name, checked)
        }
    }

    fun update(checked: Boolean) = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.updateSearchItemUnchecked(checked)
        }
    }

    fun delete(name: String) = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.deleteCityByName(name)
        }
    }

    fun delete(checked: Boolean) = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.deleteCheckedCity(checked)
        }
    }

    fun deleteAll() = viewModelScope.launch {
        withContext(contextProvider.IO) {
            searchDao.deleteall()
        }
    }
}