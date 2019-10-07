package com.example.springfragmenterclient.fragments

import android.database.Cursor
import android.database.MatrixCursor
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.utils.GsonRequest

class SearchPhraseViewModel : ViewModel() {
    var lines: List<Line> = emptyList()

    fun getMoviesByPhraseRequest(phrase: String, successListener: () -> Unit, errorListener: (VolleyError) -> Unit) =
        GsonRequest<List<Line>>(
            "${Fragmentator4000.apiUrl}/searchPhrase?phrase=$phrase&firstResult=0&maxResults=1000",
            Fragmentator4000.linesListType,
            mutableMapOf(),
            Response.Listener { response ->
                lines = response
                successListener.invoke()
            },
            Response.ErrorListener { error ->
                errorListener.invoke(error)
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

    fun getHints(phrase: String, successListener: (Cursor) -> Unit, errorListener: (VolleyError) -> Unit) =
        GsonRequest<List<Line>>(
            "${Fragmentator4000.apiUrl}/lineHints?phrase=$phrase",
            Fragmentator4000.linesListType,
            mutableMapOf(),
            Response.Listener { response ->
                val hints: List<Line> = response
                val cursor = MatrixCursor(arrayOf("_id", "hint"))
                hints.forEach { hint ->
                    val rowBuilder = cursor.newRow()
                    rowBuilder.apply {
                        add("_id", hint.id)
                        add("hint", hint.textLines)
                    }
                }
                successListener.invoke(cursor)
            },
            Response.ErrorListener { error ->
                errorListener.invoke(error)
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
}
