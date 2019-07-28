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

class SelectedMovie : AppCompatActivity() {

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
        //recyclerView.adapter = LineRecyclerViewAdapter(selectedMovie.subtitles.filteredLines)
        RequestQueueSingleton.getInstance(this)
            .addToRequestQueue(
                getLines(Fragmentator4000.encodeValue("${selectedMovie.path}/${selectedMovie.subtitles.filename}")))
        selectButton.setOnClickListener {
            val intent = Intent(applicationContext,FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE",selectedMovie)
                putExtra("ENDPOINT", "${Fragmentator4000.apiUrl}/dialog")
            }
            startActivity(intent)
        }
    }

    fun getLines(fileName: String): JsonArrayRequest {
        return JsonArrayRequest(
            Request.Method.GET, "${Fragmentator4000.apiUrl}/subtitles?fileName=$fileName", null,
            Response.Listener { response ->
                val gson = Gson()
                lines = gson.fromJson(response.toString(), Fragmentator4000.linesListType)
                recyclerView.adapter = DialogLineRecyclerViewAdapter(lines).apply {
                    setOnLinesSelectedListener {
                        if (selectedItems.size() >= 2) {
                            selectButton.isEnabled = true
                            selectedMovie.subtitles.filteredLines = lines.filter {
                                selectedItems.get(lines.lastIndexOf(it), false)
                            }.toMutableList()
                        } else {
                            selectButton.isEnabled = false
                        }
                    }
                }
                //progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener { error ->
                Toast.makeText(applicationContext, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                //progressBar.visibility = View.INVISIBLE
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }
    }
}
