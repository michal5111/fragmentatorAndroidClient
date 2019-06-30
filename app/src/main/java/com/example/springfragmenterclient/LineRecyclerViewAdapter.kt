package com.example.springfragmenterclient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Entities.Line

class LineRecyclerViewAdapter(private val dataSet: List<Line>) : RecyclerView.Adapter<LineRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val timeTextView: TextView = v.findViewById(R.id.TimeTextView)
        val lineTextView: TextView = v.findViewById(R.id.LineTextView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.line_row_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.timeTextView.text = dataSet[position].timeString
        viewHolder.lineTextView.text = dataSet[position].textLines
        viewHolder.lineTextView.setOnClickListener {
            (viewHolder.lineTextView.context as MainActivity).selectMovie(dataSet[position].parent,dataSet[position])
        }
        viewHolder.timeTextView.setOnClickListener {
            (viewHolder.lineTextView.context as MainActivity).selectMovie(dataSet[position].parent,dataSet[position])
        }
    }
    override fun getItemCount() = dataSet.size
}