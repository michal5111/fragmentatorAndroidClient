package com.example.springfragmenterclient

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Entities.Movie
import com.google.gson.Gson
import org.json.JSONArray

class SelectedMovieActivity : AppCompatActivity() {

    private lateinit var selectedMovie: Movie
    private lateinit var lines: List<Line>
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_movie)
        selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        recyclerView = findViewById(R.id.RecyclerView)
        selectButton = findViewById(R.id.button)
        val viewManager = LinearLayoutManager(this)
        val movieTitleTextView: TextView = findViewById(R.id.movieTitle)
        movieTitleTextView.text = selectedMovie.fileName
        recyclerView.layoutManager = viewManager
        RequestQueueSingleton.getInstance(this)
            .addToRequestQueue(
                getLines(selectedMovie.id)
            )
        selectButton.setOnClickListener {
            val intent = Intent(applicationContext, FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", selectedMovie)
                putExtra("ENDPOINT", "/dialog")
            }
            startActivity(intent)
        }
    }

    private fun getLines(movieId: Long) = JsonArrayRequest(
        Request.Method.GET, "${Fragmentator4000.apiUrl}/getLines?movieId=$movieId", null,
        Response.Listener { response -> onResponseListener(response) },
        Response.ErrorListener { error ->
            Toast.makeText(applicationContext, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    ).apply {
        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    private fun onLinesSelectedListener(adapter: DialogLineRecyclerViewAdapter) {
        if (adapter.selectedItems.size() >= 2) {
            selectButton.isEnabled = true
            selectedMovie.subtitles.filteredLines = lines.filter {
                adapter.selectedItems.get(lines.lastIndexOf(it), false)
            }.toMutableList()
        } else {
            selectButton.isEnabled = false
        }
    }

    private fun onResponseListener(response: JSONArray) {
        val gson = Gson()
        lines = gson.fromJson(response.toString(), Fragmentator4000.linesListType)
        recyclerView.adapter = DialogLineRecyclerViewAdapter(lines).apply {
            setOnLinesSelectedListener { adapter -> onLinesSelectedListener(adapter) }
        }
        if (intent.hasExtra("POSITION")) {
            val position: Int = intent.getIntExtra("POSITION", 0)
            recyclerView.apply {
                scrollToPosition(position)
                (adapter as DialogLineRecyclerViewAdapter).apply {
                    selectedItems.put(position, true)
                    notifyDataSetChanged()
                }
            }
        }
    }
}
