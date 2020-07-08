package com.example.weatherdemo.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.R
import com.example.weatherdemo.api.WeatherService
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.data.model.WeatherResponse
import com.example.weatherdemo.databinding.ItemSearchHistoryBinding
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.util.OnItemLongClick
import dagger.hilt.android.scopes.ActivityScoped
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityScoped
class SearchHistoryAdapter @Inject constructor(private val weatherService: WeatherService): RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryItemViewHolder>() {

    private lateinit var onItemClick: OnItemClick
    private lateinit var onItemLongClick: OnItemLongClick

    var selected = false

    private var cities = emptyList<SearchHistoryItem>()

    private val compositeDisposable = CompositeDisposable()

    inner class SearchHistoryItemViewHolder(private val itemSearchHistoryBinding: ItemSearchHistoryBinding, private val onItemClick: OnItemClick)
        : RecyclerView.ViewHolder(itemSearchHistoryBinding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            itemSearchHistoryBinding.root.findViewById<ConstraintLayout>(R.id.cl_search)?.apply {
                setOnClickListener(this@SearchHistoryItemViewHolder)
                setOnLongClickListener(this@SearchHistoryItemViewHolder)
            }
        }

        fun bind(searchHistoryItem: SearchHistoryItem) {
            itemSearchHistoryBinding.searchItem = searchHistoryItem
            itemSearchHistoryBinding.executePendingBindings()
        }

        override fun onClick(view: View?) {
            view?.let {
                if (selected) {
                    onItemLongClick.onItemLongClick(view, adapterPosition)
                } else {
                    onItemClick.onItemClick(view, adapterPosition)
                }
            }
        }

        override fun onLongClick(p0: View?): Boolean {
            p0?.let {
                selected = true
            }
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : SearchHistoryItemViewHolder {
        return SearchHistoryItemViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_search_history, parent, false), onItemClick)
    }

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {
        var city = cities[position]
        compositeDisposable.add(
            weatherService.getWeatherByCityNameRx(cities[position].name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherResponse>(){
                    override fun onSuccess(t: WeatherResponse) {
                        city.detail = t.list[0].weather[0].description
                        city.icon = t.list[0].weather[0].icon
                        city.temp = t.list[0].temp.day
                        holder.bind(city)
                    }

                    override fun onError(e: Throwable) {
                        holder.bind(city)
                    }
                })
        )
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        compositeDisposable.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }

    internal fun setCities(cities: List<SearchHistoryItem>) {
        this.cities = cities
        notifyDataSetChanged()
    }

    override fun getItemCount() = cities.size

    fun setOnItemClick(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }

    fun setOnItemLongClick(onItemLongClick: OnItemLongClick) {
        this.onItemLongClick = onItemLongClick
    }
}