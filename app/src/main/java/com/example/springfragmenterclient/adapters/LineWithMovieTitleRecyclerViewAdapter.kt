package com.example.springfragmenterclient.adapters

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.model.Line

class LineWithMovieTitleRecyclerViewAdapter :
    PagedListAdapter<Line, LineWithMovieTitleRecyclerViewAdapter.ViewHolder>(object :
        DiffUtil.ItemCallback<Line>() {
        override fun areItemsTheSame(oldItem: Line, newItem: Line): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Line, newItem: Line): Boolean {
            return oldItem == newItem
        }
    }) {

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
        val line: Line? = getItem(position)
        if (line != null) {
            viewHolder.apply {
                titleTextView.text = line.subtitles.movie.fileName
                timeTextView.text = line.timeString
                lineTextView.text =
                    HtmlCompat.fromHtml(line.textLines, Html.FROM_HTML_MODE_LEGACY)
                cardView.setOnClickListener {
                    (this.lineTextView.context as MainActivity).selectLine(
                        line.subtitles.movie,
                        line
                    )
                }
            }
        }
    }

//    override fun getItemCount() = dataSet.size
}