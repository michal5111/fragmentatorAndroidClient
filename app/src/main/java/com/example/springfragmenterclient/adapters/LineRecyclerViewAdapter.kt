package com.example.springfragmenterclient.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.model.Line

class LineRecyclerViewAdapter(private val dataSet: List<Line>) : RecyclerView.Adapter<LineRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val timeTextView: TextView = v.findViewById(R.id.TimeTextView)
        val lineTextView: TextView = v.findViewById(R.id.LineTextView)
        val cardView: CardView = v.findViewById(R.id.CardView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.line_row_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            timeTextView.text = dataSet[position].timeString
            lineTextView.text =
                HtmlCompat.fromHtml(dataSet[position].textLines, HtmlCompat.FROM_HTML_MODE_COMPACT)
            cardView.setOnClickListener {
                (this.lineTextView.context as MainActivity).selectLine(dataSet[position].subtitles.movie,dataSet[position])
            }
        }
    }
    override fun getItemCount() = dataSet.size
}