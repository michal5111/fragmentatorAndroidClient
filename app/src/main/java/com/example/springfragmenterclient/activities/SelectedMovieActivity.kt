package com.example.springfragmenterclient.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.DialogLineRecyclerViewAdapter
import com.example.springfragmenterclient.entities.FragmentRequest
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.entities.Movie
import com.example.springfragmenterclient.utils.GsonRequest
import com.example.springfragmenterclient.utils.RequestQueueSingleton

class SelectedMovieActivity : AppCompatActivity() {

    private lateinit var selectedMovie: Movie
    private lateinit var lines: List<Line>
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectButton: Button
    private lateinit var filterSearchView: SearchView
    private var fragmentRequest: FragmentRequest = FragmentRequest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_movie)
        selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        recyclerView = findViewById(R.id.RecyclerView)
        selectButton = findViewById(R.id.button)
        fragmentRequest.apply {
            movieId = selectedMovie.id
        }
        val viewManager = LinearLayoutManager(this)
        val movieTitleTextView: TextView = findViewById(R.id.movieTitle)
        movieTitleTextView.text = selectedMovie.fileName
        recyclerView.layoutManager = viewManager
        RequestQueueSingleton.getInstance(this).addToRequestQueue(getLines(selectedMovie.id))
        selectButton.setOnClickListener {
            val intent = Intent(applicationContext, FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", selectedMovie)
                putExtra("FRAGMENT_REQUEST", fragmentRequest)
            }
            startActivity(intent)
        }
        filterSearchView = findViewById(R.id.filterSearchView)
        filterSearchView.setOnQueryTextListener(onFilterQueryTextListener)
    }

    private fun getLines(movieId: Long) = GsonRequest<List<Line>>(
        "${Fragmentator4000.apiUrl}/getLines?movieId=$movieId",
        Fragmentator4000.linesListType,
        mutableMapOf(),
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
        if (filterSearchView.query.isNotBlank()) {
            filterSearchView.setQuery("", false)
            //recyclerView.scrollTo()
        }
        if (adapter.selectedItems.size() >= 2) {
            selectButton.isEnabled = true
            fragmentRequest.apply {
                startLineId = lines.first().id
                stopLineId = lines.last().id
            }
        } else {
            selectButton.isEnabled = false
        }
    }

    private fun onResponseListener(response: List<Line>) {
        lines = response
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

    private val onFilterQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            (recyclerView.adapter as DialogLineRecyclerViewAdapter).filter.filter(p0)
            return false
        }

    }
}
