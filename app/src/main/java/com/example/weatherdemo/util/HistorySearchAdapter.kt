package com.example.weatherdemo.util

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cursoradapter.widget.ResourceCursorAdapter
import com.example.weatherdemo.R

class HistorySearchAdapter(context: Context, resource: Int, cursor: Cursor?) :ResourceCursorAdapter(context, resource, cursor) {

    var onHistoryDeleteClickListener: OnHistoryDeleteClickListener? = null
    fun setListener(onHistoryDeleteClickListener:OnHistoryDeleteClickListener){
        this.onHistoryDeleteClickListener = onHistoryDeleteClickListener
    }

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_search_history_list, parent, false)

        val viewHolder = ViewHolder(view)
        view.tag = viewHolder

        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewHolder = view.tag as ViewHolder
        val itemId: Int = cursor.getInt(cursor.getColumnIndex("_id"))
        val str = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        viewHolder.searchStr.text = str
        viewHolder.deleteBtn.setOnClickListener{
            if(onHistoryDeleteClickListener != null){
                onHistoryDeleteClickListener!!.onItemClick(itemId.toString())
                notifyDataSetChanged()
            }
        }
    }

    fun getSuggestionText(position: Int): String? {
        if (position >= 0 && position < cursor.count) {
            val cursor = cursor
            cursor.moveToPosition(position)
            return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
        }
        return null
    }

    class ViewHolder(view: View) {
        var deleteBtn: ImageView = view.findViewById<View>(R.id.deleteBtn) as ImageView
        var searchStr: TextView = view.findViewById<View>(R.id.searchStr) as TextView

    }
}