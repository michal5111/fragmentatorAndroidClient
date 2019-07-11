package com.example.springfragmenterclient

import android.app.Application
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
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
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class Fragmentator4000 : Application() {

    private var movies: List<Movie> = emptyList()
    companion object {
        const val url = "http://michal5111.asuscomm.com:8080"
        val movieListType = object : TypeToken<List<Movie>>() {}.type!!

        fun timeToSeconds(time: String): Double {
            val split = time.split(":")
            if (split.size < 3) return 0.0
            val hours = split[0].toDouble()
            val minutes = split[1].toDouble()
            val seconds = split[2].toDouble()
            return hours * 3600 + minutes * 60 + seconds
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
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
                Toast.makeText(applicationContext,"error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
        )
        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        return jsonArrayRequest
    }

    fun getSnapshotRequest(movie: JSONObject, networkImageView: NetworkImageView, imageLoader: ImageLoader, progressBar: ProgressBar) : JsonObjectRequest {
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, "$url/rest/linesnapshot", movie,
            Response.Listener { response ->
                val gson = Gson()
                val json = gson.fromJson(response.toString(), com.example.springfragmenterclient.Entities.Response::class.java)
                networkImageView.setImageUrl(json.url,imageLoader)
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener { error ->
                progressBar.visibility = View.INVISIBLE
                Toast.makeText(applicationContext,"error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        )
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            1000,
            20,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        return jsonObjectRequest
    }
}