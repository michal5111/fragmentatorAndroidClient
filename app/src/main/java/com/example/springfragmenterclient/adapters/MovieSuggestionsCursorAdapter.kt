package com.example.springfragmenterclient.adapters

import android.content.Context
import android.database.Cursor
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.SearchView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.example.springfragmenterclient.R

class MovieSuggestionsCursorAdapter(context: Context, c: Cursor, autoRequery: Boolean,
                                    private val searchView: SearchView
) :
    CursorAdapter(context, c, autoRequery) {

    private val mLayoutInflater = LayoutInflater.from(context)

    override fun newView(context: Context, cursor: Cursor, viewGroup: ViewGroup): View? {
        return mLayoutInflater.inflate(R.layout.hint_row_item,viewGroup,false)
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val htmlLine: String = cursor.getString(cursor.getColumnIndexOrThrow("hint"))
        val line = HtmlCompat.fromHtml(htmlLine, Html.FROM_HTML_MODE_LEGACY)
        val textView: TextView = view.findViewById(R.id.TitleTextView)
        textView.apply {
            text = line
            setOnClickListener {
                searchView.setQuery(htmlLine,true)
                searchView.clearFocus()
            }
        }
    }
}
