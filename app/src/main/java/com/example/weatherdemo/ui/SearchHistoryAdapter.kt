package com.example.weatherdemo.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.R
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.util.OnItemLongClick
import timber.log.Timber

class SearchHistoryAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryItemViewHolder>() {

    private lateinit var onItemClick: OnItemClick
    private lateinit var onItemlLongClick: OnItemLongClick

    var selected = false

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cities = emptyList<SearchHistoryItem>()

    inner class SearchHistoryItemViewHolder(itemView: View, private val onItemClick: OnItemClick)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.findViewById<TextView>(R.id.textView)?.apply {
                setOnClickListener(this@SearchHistoryItemViewHolder)
                setOnLongClickListener(this@SearchHistoryItemViewHolder)
            }
        }

        val searchHistoryItemView: TextView = itemView.findViewById(R.id.textView)

        val searchRelativeLayout: RelativeLayout = itemView.findViewById(R.id.rl_search)

        override fun onClick(view: View?) {
            view?.let {
                if (selected) {
                    onItemlLongClick.onItemLongClick(view, adapterPosition)
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
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return SearchHistoryItemViewHolder(itemView, onItemClick)
    }

    override fun onBindViewHolder(holder: SearchHistoryItemViewHolder, position: Int) {
        val current = cities[position]
        holder.searchHistoryItemView.apply {
            text = current.name
        }
        holder.searchRelativeLayout.apply {
            setBackgroundColor(if (current.checked) Color.GRAY else Color.TRANSPARENT)
        }
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
        this.onItemlLongClick = onItemLongClick
    }
}