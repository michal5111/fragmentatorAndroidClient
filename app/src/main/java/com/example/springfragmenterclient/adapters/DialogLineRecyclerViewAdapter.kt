package com.example.springfragmenterclient.adapters

import android.graphics.Color
import android.text.Html
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.R

class DialogLineRecyclerViewAdapter(private val dataSet: List<Line>) :
    RecyclerView.Adapter<DialogLineRecyclerViewAdapter.ViewHolder>() {

    val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private var dataSelectedListener: ((adapter: DialogLineRecyclerViewAdapter) -> Unit)? = null

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
            timeTextView.apply {
                text = dataSet[position].timeString
            }
            lineTextView.apply {
                text = HtmlCompat.fromHtml(dataSet[position].textLines, Html.FROM_HTML_MODE_LEGACY)
            }
            cardView.setOnClickListener {
                if (selectedItems.get(position, false)) {
                    for (i in position until position + selectedItems.size()) {
                        selectedItems.delete(i)
                    }

                } else {
                    selectedItems.put(position, true)
                    for (i in selectedItems.keyAt(0)..selectedItems.keyAt(selectedItems.size() - 1)) {
                        selectedItems.put(i, true)
                    }
                }
                notifyDataSetChanged()
                dataSelectedListener?.invoke(this@DialogLineRecyclerViewAdapter)
            }
        }
        if (selectedItems.get(position,false)) {
            viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.cardView.context,
                R.color.colorPrimary
            ))
        } else {
            viewHolder.cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount() = dataSet.size

    fun setOnLinesSelectedListener(onLinesSelectedListener: (adapter: DialogLineRecyclerViewAdapter) -> Unit) {
        dataSelectedListener = onLinesSelectedListener
    }
}