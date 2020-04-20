package com.example.weatherdemo.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdemo.R
import com.example.weatherdemo.data.model.SearchHistoryItem
import com.example.weatherdemo.util.OnItemClick
import com.example.weatherdemo.util.OnItemLongClick

class SearchHistoryAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<SearchHistoryAdapter.SearchHistoryItemViewHolder>() {

    private lateinit var onItemClick: OnItemClick
    private lateinit var onItemlLongClick: OnItemLongClick

    var selected = false

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cities = emptyList<SearchHistoryItem>()

    inner class SearchHistoryItemViewHolder(itemView: View, val onItemClick: OnItemClick)
        : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

        init {
            itemView.findViewById<TextView>(R.id.textView).apply {
                setOnClickListener(this@SearchHistoryItemViewHolder)
                setOnLongClickListener(this@SearchHistoryItemViewHolder)
            }
        }

        val searchHistoryItemView: TextView = itemView.findViewById(R.id.textView)

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
            setBackgroundColor(if (current.checked) Color.BLUE else Color.TRANSPARENT)
        }
    }

    internal fun setCities(words: List<SearchHistoryItem>) {
        this.cities = words
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