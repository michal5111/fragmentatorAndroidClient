package com.example.springfragmenterclient.fragments

import android.database.Cursor
import android.database.MatrixCursor
import androidx.lifecycle.ViewModel
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.VolleyError
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.entities.Movie
import com.example.springfragmenterclient.utils.GsonRequest

class SearchMovieViewModel : ViewModel() {
    var movies: List<Movie> = emptyList()

    fun getMoviesByTitleRequest(title: String, successListener: () -> Unit, errorListener: (VolleyError) -> Unit) =
        GsonRequest<List<Movie>>(
            "${Fragmentator4000.apiUrl}/searchMovie?title=$title",
            Fragmentator4000.movieListType,
            mutableMapOf(),
            Response.Listener { response ->
                movies = response
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

    fun getHints(title: String, successListener: (Cursor) -> Unit, errorListener: (VolleyError) -> Unit) =
        GsonRequest<List<Movie>>(
            "${Fragmentator4000.apiUrl}/movieHints?title=$title",
            Fragmentator4000.movieListType,
            mutableMapOf(),
            Response.Listener { response ->
                val hints: List<Movie> = response
                val cursor = MatrixCursor(arrayOf("_id", "hint"))
                hints.forEach { hint ->
                    val rowBuilder = cursor.newRow()
                    rowBuilder.apply {
                        add("_id", hint.id)
                        add("hint", hint.fileName)
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
