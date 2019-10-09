package com.example.springfragmenterclient.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.entities.Movie

class LineWithMovieTitleRecyclerViewAdapter(private val dataSetFull: List<Line>)
    : RecyclerView.Adapter<LineWithMovieTitleRecyclerViewAdapter.ViewHolder>(), Filterable {

    private val dataSet = mutableListOf<Line>()

    override fun getFilter(): Filter {
        return filterByTitle
    }

    private val filterByTitle = object : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val filteredList: MutableList<Line> = emptyList<Line>().toMutableList()
            if (p0.isNullOrBlank()) {
                filteredList.addAll(dataSetFull)
            } else {
                val pattern = p0.toString().toUpperCase().replace('.', ' ').trim()
                dataSetFull.forEach {
                    if (it.subtitles.movie.fileName.toUpperCase().replace('.', ' ').contains(pattern)) {
                        filteredList.add(it)
                    }
                }
            }
            val filteredResults = FilterResults()
            filteredResults.values = filteredList
            return filteredResults
        }

        override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
            dataSet.clear()
            dataSet.addAll(p1!!.values as MutableList<Line>)
            notifyDataSetChanged()
        }

    }

    init {
        dataSet.addAll(dataSetFull)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleTextView: TextView = v.findViewById(R.id.TitleTextView)
        val timeTextView: TextView = v.findViewById(R.id.TimeTextView)
        val lineTextView: TextView = v.findViewById(R.id.LineTextView)
        val cardView: CardView = v.findViewById(R.id.CardView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.line_with_movie_title_row_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            titleTextView.text = dataSet[position].subtitles.movie.fileName
            timeTextView.text = dataSet[position].timeString
            lineTextView.text = HtmlCompat.fromHtml(dataSet[position].textLines, Html.FROM_HTML_MODE_LEGACY)
            cardView.setOnClickListener {
                (this.lineTextView.context as MainActivity).selectLine(dataSet[position].subtitles.movie,dataSet[position])
            }
        }
    }
    override fun getItemCount() = dataSet.size
}