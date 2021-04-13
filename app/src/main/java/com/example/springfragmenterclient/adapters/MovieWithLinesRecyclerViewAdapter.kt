package com.example.springfragmenterclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.model.Movie
import java.util.*

class MovieWithLinesRecyclerViewAdapter(private val dataSetFull: List<Movie>) :
    RecyclerView.Adapter<MovieWithLinesRecyclerViewAdapter.ViewHolder>(), Filterable {

    private val dataSet = mutableListOf<Movie>()

    override fun getFilter(): Filter {
        return filterByTitle
    }

    init {
        dataSet.addAll(dataSetFull)
    }

    private val filterByTitle = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredList: MutableList<Movie> = emptyList<Movie>().toMutableList()
            if (p0.isNullOrBlank()) {
                filteredList.addAll(dataSetFull)
            } else {
                val pattern = p0.toString().toUpperCase(Locale.ROOT).replace('.', ' ').trim()
                dataSetFull.forEach {
                    if (it.fileName.toUpperCase(Locale.ROOT).replace('.', ' ').contains(pattern)) {
                        filteredList.add(it)
                    }
                }
            }
            val filteredResults = FilterResults()
            filteredResults.values = filteredList
            return filteredResults
        }

        override fun publishResults(cs: CharSequence?, fr: FilterResults?) {
            fr?.let {
                dataSet.clear()
                dataSet.addAll(fr.values as MutableList<Movie>)
                notifyDataSetChanged()
            }
        }

    }


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleTextView: TextView = v.findViewById(R.id.TitleTextView)
        val lineRecyclerView: RecyclerView = v.findViewById(R.id.LineRecycleView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.movie_with_lines_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            lineRecyclerView.layoutManager = LinearLayoutManager(viewHolder.lineRecyclerView.context)
            titleTextView.text = dataSet[position].fileName
        }
    }
    override fun getItemCount() = dataSet.size
}