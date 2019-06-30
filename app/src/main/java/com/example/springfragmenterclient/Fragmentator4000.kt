package com.example.springfragmenterclient

import android.app.Application
import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.NetworkImageView
import com.example.springfragmenterclient.Entities.Movie
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


class Fragmentator4000 : Application() {

    var movies: List<Movie> = emptyList()
    companion object {
        val url = "http://michal5111.asuscomm.com:8080"
        val movieListType = object : TypeToken<List<Movie>>() {}.type
    }

    fun getMoviesRequest(fraze: String, recyclerView: RecyclerView, progressBar: ProgressBar): JsonArrayRequest {
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, "$url/rest/search?fraze=$fraze", null,
            Response.Listener { response ->
                val gson = Gson()
                movies = gson.fromJson(response.toString(), movieListType)
                recyclerView.adapter = MovieRecyclerViewAdapter(movies)
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener { error ->
                println("ERROR: ${error.message}")
                println(error.localizedMessage)
                progressBar.visibility = View.INVISIBLE
            }
        )
        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        return jsonArrayRequest
    }

    fun getSnapshotRequest(movie: JSONObject, networkImageView: NetworkImageView, imageLoader: ImageLoader) : JsonObjectRequest {
        val jsonArrayRequest = JsonObjectRequest(Request.Method.POST, "$url/rest/linesnapshot", movie,
            Response.Listener { response ->
                val gson = Gson()
                val json = gson.fromJson(response.toString(), com.example.springfragmenterclient.Entities.Response::class.java)
                networkImageView.setImageUrl(json.url,imageLoader)
            },
            Response.ErrorListener { error ->
                println("ERROR: ${error.message}")
                println(error.localizedMessage)
            }
        )
        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        return jsonArrayRequest
    }
}