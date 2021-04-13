package com.example.springfragmenterclient.adapters

import android.graphics.Color
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.model.Line
import java.util.*

class DialogLineRecyclerViewAdapter(private val dataSetFull: List<Line>) :
    RecyclerView.Adapter<DialogLineRecyclerViewAdapter.ViewHolder>(), Filterable {

    private val dataSet = mutableListOf<Line>()

    override fun getFilter(): Filter {
        return filterByTitle
    }

    init {
        dataSet.addAll(dataSetFull)
    }

    private val filterByTitle = object : Filter() {
        override fun performFiltering(sequence: CharSequence?): FilterResults {
            val filteredList: MutableList<Line> = emptyList<Line>().toMutableList()
            if (sequence.isNullOrBlank()) {
                filteredList.addAll(dataSetFull)
            } else {
                val pattern = sequence.toString().toUpperCase(Locale.ROOT).trim()
                dataSetFull.forEach {
                    if (it.textLines.toUpperCase(Locale.ROOT).contains(pattern)) {
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
                dataSet.addAll(it.values as MutableList<Line>)
                notifyDataSetChanged()
            }
        }

    }

    val selectedItems = SparseBooleanArray()

    private var dataSelectedListener: ((adapter: DialogLineRecyclerViewAdapter, position: Int) -> Unit)? =
        null

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
                text = HtmlCompat.fromHtml(
                    dataSet[position].textLines,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            }
            cardView.setOnClickListener {
                if (selectedItems.get(position, false)) {
                    for (i in position until position + selectedItems.size()) {
                        selectedItems.delete(i)
                        notifyItemChanged(i)
                    }

                } else {
                    selectedItems.put(dataSetFull.indexOf(dataSet[position]), true)
                    for (i in selectedItems.keyAt(0)..selectedItems.keyAt(selectedItems.size() - 1)) {
                        selectedItems.put(i, true)
                        notifyItemChanged(i)
                    }
                }
                dataSelectedListener?.invoke(
                    this@DialogLineRecyclerViewAdapter,
                    dataSetFull.indexOf(dataSet[position])
                )
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

    fun setOnLinesSelectedListener(onLinesSelectedListener: (adapter: DialogLineRecyclerViewAdapter, position: Int) -> Unit) {
        dataSelectedListener = onLinesSelectedListener
    }
}