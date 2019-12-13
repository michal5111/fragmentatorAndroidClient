package com.example.springfragmenterclient.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.entities.Line

class LineEditViewAdapter(private val dataSet: List<Line>) :
    RecyclerView.Adapter<LineEditViewAdapter.ViewHolder>() {

    private var beforeChange: CharSequence = ""

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var edited: Boolean = false
        val lineTextEdit: EditText = v.findViewById(R.id.LineTextEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.line_edit_row_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val lineEditTextWatcher = object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {
                holder.edited = p0!! != beforeChange
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                beforeChange = p0!!
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        }
        holder.apply {
            lineTextEdit.setText(dataSet[position].textLines)
            lineTextEdit.addTextChangedListener(lineEditTextWatcher)
        }
    }
}

