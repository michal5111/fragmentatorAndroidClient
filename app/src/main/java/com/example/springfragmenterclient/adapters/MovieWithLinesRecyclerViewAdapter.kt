package com.example.springfragmenterclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.utils.RequestQueueSingleton
import com.google.gson.Gson

class MovieWithLinesRecyclerViewAdapter(private val dataSet: List<Movie>, private val fraze: String, private val context: Context) : RecyclerView.Adapter<MovieWithLinesRecyclerViewAdapter.ViewHolder>() {

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
            RequestQueueSingleton.getInstance(context)
                .addToRequestQueue(getFilteredLines(position,lineRecyclerView))

        }
    }
    override fun getItemCount() = dataSet.size

    private fun getFilteredLines(position: Int, lineRecyclerView: RecyclerView) = JsonArrayRequest(
        Request.Method.GET, "${Fragmentator4000.apiUrl}/getFilteredLines?subtitlesId=${dataSet[position].subtitles.id}&fraze=$fraze", null,
        Response.Listener { response ->
            val gson = Gson()
            dataSet[position].subtitles.filteredLines = gson.fromJson(response.toString(),
                Fragmentator4000.linesListType
            )
            for (line in dataSet[position].subtitles.filteredLines) {
                line.parent = dataSet[position]
            }
            lineRecyclerView.adapter =
                LineRecyclerViewAdapter(dataSet[position].subtitles.filteredLines)
        },
        Response.ErrorListener { error ->
        }
    ).apply {
        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }
}