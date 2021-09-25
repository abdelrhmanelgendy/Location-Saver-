package com.example.locationsaver.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsaver.R
import com.example.locationsaver.pojo.HistorySearchedLocations

class SearchHistoryAdapter(val context: Context) :
    RecyclerView.Adapter<SearchHistoryAdapter.HistoryHolder>() {

    var locationHistoryList: ArrayList<HistorySearchedLocations> = ArrayList()
        set(value) {
            field = value
        }

    companion object {
        lateinit var onHistoryItemClickListener: onHistoryItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.custome_item_searched_item, null)
        return HistoryHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        val history = locationHistoryList.get(position)
        holder.bind(history)
    }

    override fun getItemCount(): Int {
        return locationHistoryList.size
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewSearchEntered: TextView
        var textViewSearchResult: TextView


        init {
            textViewSearchEntered =
                itemView.findViewById(R.id.custome_searched_item_searchItem_TV_enteredText)
            textViewSearchResult =
                itemView.findViewById(R.id.custome_searched_item_searchItem_TV_actualResult)


        }


        fun bind(historySearchedLocations: HistorySearchedLocations) {
            textViewSearchEntered.setText(historySearchedLocations.searchEntered)
            textViewSearchResult.setText(historySearchedLocations.searchResult)
            if (adapterPosition != RecyclerView.NO_POSITION) {
                itemView.setOnClickListener {
                    onHistoryItemClickListener.onItemClick(historySearchedLocations)
                }
            }

        }

    }


}

interface onHistoryItemClickListener {
    fun onItemClick(historySearchedLocations: HistorySearchedLocations)
}