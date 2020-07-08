package com.example.weatherdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.ui.SearchHistoryAdapter
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.util.OnItemLongClick
import com.example.weatherdemo.viewmodel.SearchViewModel
import com.example.weatherdemo.viewmodel.SearchViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.search_bottom_sheet_layout.*
import kotlinx.android.synthetic.main.search_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity: AppCompatActivity(), OnItemClick, OnItemLongClick {

    @Inject
    lateinit var factory: WeatherViewModelFactory

    private lateinit var weatherViewModel: WeatherViewModel

    @Inject
    lateinit var searchFactory: SearchViewModelFactory

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var cities: List<SearchHistoryItem>

    private lateinit var adapter: SearchHistoryAdapter

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_layout)

        bottomSheetBehavior = BottomSheetBehavior.from(searchbottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        })

        search_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val city = search_text.text.toString()
                if (city.isNotEmpty()) {
                    searchViewModel.insert(SearchHistoryItem(city))
                    setResult(Activity.RESULT_OK, Intent().putExtra("city", city))
                    resetCheckedStates()
                    finish()
                }
            }
            false
        }

        search_text.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                resetCheckedStates()
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.searchHistory)
        adapter = SearchHistoryAdapter(this)
        recyclerView.adapter = adapter
        adapter.setOnItemClick(this)
        adapter.setOnItemLongClick(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        searchViewModel = ViewModelProviders.of(this, searchFactory).get(SearchViewModel::class.java)

        searchViewModel.searchHistoryLivaData.observe(this, Observer {
            t ->
            t?.let {
                cities = it
                adapter.setCities(t)
            }
        })

        weatherViewModel = ViewModelProviders.of(this, factory).get(WeatherViewModel::class.java)

        weatherViewModel.weatherLivaData.observe(this,
            Observer { t -> t?.let {

            }})

        deleteSelectedItems.setOnClickListener {
            searchViewModel.delete(true)
            adapter.selected = false
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        cancelAll.setOnClickListener {
            resetCheckedStates()
        }
    }

    override fun onPause() {
        resetCheckedStates()
        super.onPause()
    }


    override fun onItemClick(view: View, position: Int) {
        when (view) {
            is TextView -> {
                val city = cities[position].name
                setResult(Activity.RESULT_OK, Intent().putExtra("city", city))
                finish()
            }
        }
    }

    override fun onItemLongClick(view: View, position: Int) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(search_text.windowToken, 0)
        search_text.clearFocus()
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
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
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        searchViewModel.update(false)
        adapter.selected = false
    }
}