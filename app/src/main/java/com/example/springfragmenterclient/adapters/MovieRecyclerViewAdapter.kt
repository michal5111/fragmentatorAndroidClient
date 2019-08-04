package com.example.springfragmenterclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity

class MovieRecyclerViewAdapter(private val dataSet: List<Movie>) :
    RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val titleTextView: TextView = v.findViewById(R.id.TitleTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.movie_row_item, viewGroup, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            titleTextView.apply {
                text = dataSet[position].fileName
                setOnClickListener {
                    (this.context as MainActivity).selectMovie(dataSet[position])
                }
            }
        }
    }

    override fun getItemCount() = dataSet.size
}