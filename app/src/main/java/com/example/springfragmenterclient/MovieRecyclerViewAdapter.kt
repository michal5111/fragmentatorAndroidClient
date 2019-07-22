package com.example.springfragmenterclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Entities.Movie

class MovieRecyclerViewAdapter(private val dataSet: List<Movie>) : RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleTextView: TextView = v.findViewById(R.id.TitleTextView)
        val lineRecyclerView: RecyclerView = v.findViewById(R.id.LineRecycleView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.movie_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.lineRecyclerView.layoutManager = LinearLayoutManager(viewHolder.lineRecyclerView.context)
        viewHolder.titleTextView.text = dataSet[position].fileName
        for (line in dataSet[position].subtitles.filteredLines) {
            line.parent = dataSet[position]
        }
        viewHolder.lineRecyclerView.adapter = LineRecyclerViewAdapter(dataSet[position].subtitles.filteredLines)
    }
    override fun getItemCount() = dataSet.size


}