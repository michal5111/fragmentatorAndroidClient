package com.example.springfragmenterclient

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

class DialogLineRecyclerViewAdapter(private val dataSet: List<Line>) :
    RecyclerView.Adapter<DialogLineRecyclerViewAdapter.ViewHolder>() {

    val selectedItems: SparseBooleanArray = SparseBooleanArray()
    private lateinit var recyclerView: RecyclerView
    private var dataSelectedListener: (()->Unit)? = null

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
                    for (i in selectedItems.keyAt(position)..selectedItems.keyAt(selectedItems.size() - 1)) {
                        selectedItems.delete(i)
                    }

                } else {
                    selectedItems.put(position, true)
                    cardView.setCardBackgroundColor(ContextCompat.getColor(cardView.context, R.color.colorPrimary))
                    for (i in selectedItems.keyAt(0)..selectedItems.keyAt(selectedItems.size() - 1)) {
                        selectedItems.put(i, true)
                    }
                }
                notifyDataSetChanged()
                dataSelectedListener?.invoke()
            }
        }
        if (selectedItems.get(position,false)) {
            viewHolder.cardView.setCardBackgroundColor(ContextCompat.getColor(viewHolder.cardView.context,R.color.colorPrimary))
        } else {
            viewHolder.cardView.setCardBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun setOnLinesSelectedListener(onLinesSelectedListener: ()->Unit) {
        dataSelectedListener = onLinesSelectedListener
    }
}