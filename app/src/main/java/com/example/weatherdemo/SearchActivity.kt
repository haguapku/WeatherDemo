package com.example.weatherdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.ui.SearchHistoryAdapter
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.util.OnItemLongClick
import com.example.weatherdemo.viewmodel.SearchViewModelFactory
import com.example.weatherdemo.viewmodel.SearchViewModel
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.search_layout.*
import javax.inject.Inject

class SearchActivity: DaggerAppCompatActivity(), OnItemClick, OnItemLongClick {

    @Inject
    lateinit var searchFactory: SearchViewModelFactory

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var cities: List<SearchHistoryItem>

    private lateinit var adapter: SearchHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)

        val recyclerView = findViewById<RecyclerView>(R.id.searchHistory)
        adapter = SearchHistoryAdapter(this)
        recyclerView.adapter = adapter
        adapter.setOnItemClick(this)
        adapter.setOnItemLongClick(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchViewModel = ViewModelProviders.of(this, searchFactory).get(SearchViewModel::class.java)

        searchViewModel.searchHistoryLivaData.observe(this, Observer {
            t ->
            t?.let {
                cities = it
                adapter.setCities(t)
            }
        })

        search.setOnClickListener {
            val city = city.text.toString()
            if (!city.isEmpty()) {
                searchViewModel.insert(SearchHistoryItem(city))
                setResult(Activity.RESULT_OK, Intent().putExtra("city", city))
                resetCheckedStates()
                finish()
            }
        }

        empty.setOnClickListener {
            empty.visibility = View.GONE
            cancel.visibility = View.GONE
            searchViewModel.delete(true)
            adapter.selected = false
        }

        cancel.setOnClickListener {
            resetCheckedStates()
        }
    }

    override fun onPause() {
        resetCheckedStates()
        super.onPause()
    }


    override fun onItemClick(view: View, position: Int) {
        when (view) {
            is ImageView -> {
                val city = cities[position].name
                searchViewModel.delete(city)
            }
            is TextView -> {
                val city = cities[position].name
                setResult(Activity.RESULT_OK, Intent().putExtra("city", city))
                finish()
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int) {
        empty.visibility = View.VISIBLE
        cancel.visibility = View.VISIBLE
        val city = cities[position].name
        if (cities[position].checked) {
            searchViewModel.update(city, false)
        } else {
            searchViewModel.update(city, true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        resetCheckedStates()
    }

    private fun resetCheckedStates() {
        empty.visibility = View.GONE
        cancel.visibility = View.GONE
        searchViewModel.update(false)
        adapter.selected = false
    }
}