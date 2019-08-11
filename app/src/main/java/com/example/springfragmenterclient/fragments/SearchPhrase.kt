package com.example.springfragmenterclient.fragments

import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.example.springfragmenterclient.Entities.Line
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.LineSuggestionsCursorAdapter
import com.example.springfragmenterclient.adapters.MovieWithLinesRecyclerViewAdapter
import com.example.springfragmenterclient.utils.RequestQueueSingleton
import com.google.gson.Gson

class SearchPhrase : Fragment() {

    companion object {
        fun newInstance() = SearchPhrase()
    }

    private lateinit var viewModel: SearchPhraseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var hints: List<Line> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_phrase_fragment, container, false)
        searchView = root.findViewById(R.id.searchView)
        progressBar = root.findViewById(R.id.progressBar3)
        recyclerView = root.findViewById(R.id.RecyclerView)
        val viewManager = LinearLayoutManager(context)
        recyclerView.layoutManager = viewManager
        searchView.setOnQueryTextListener(onQueryTextListener)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchPhraseViewModel::class.java)
            .apply { applicationContext = activity?.applicationContext as Fragmentator4000 }
    }

    private fun getMoviesByPhraseRequest(phrase: String) = JsonArrayRequest(
            Request.Method.GET, "${Fragmentator4000.apiUrl}/searchPhrase?phrase=$phrase", null,
            Response.Listener { response ->
                val gson = Gson()
                viewModel.movies = gson.fromJson(response.toString(), Fragmentator4000.movieListType)
                recyclerView.adapter = MovieWithLinesRecyclerViewAdapter(
                    viewModel.movies,
                    phrase,
                    context!!
                )
                progressBar.visibility = View.INVISIBLE
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.INVISIBLE
            }
        ).apply {
            retryPolicy = DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        }

    private fun getHints(phrase: String) = JsonArrayRequest(
        Request.Method.GET, "${Fragmentator4000.apiUrl}/lineHints?phrase=$phrase", null,
        Response.Listener { response ->
            val gson = Gson()
            hints = gson.fromJson(response.toString(), Fragmentator4000.linesListType)
            val cursor = MatrixCursor(arrayOf("_id","hint"))
            hints.forEach { hint ->
                val rowBuilder = cursor.newRow()
                rowBuilder.apply {
                    add("_id", hint.id)
                    add("hint",hint.textLines)
                }
            }
            searchView.suggestionsAdapter = LineSuggestionsCursorAdapter(
                context!!,
                cursor,
                true,
                searchView
            )
        },
        Response.ErrorListener { error ->
            hints = emptyList()
        }
    ).apply {
        retryPolicy = DefaultRetryPolicy(
            20000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(p0: String?): Boolean {
            RequestQueueSingleton.getInstance(context!!)
                .addToRequestQueue(
                    getHints(Fragmentator4000.encodeValue(p0.toString()))
                )
            return false
        }

        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            RequestQueueSingleton.getInstance(context!!)
                .addToRequestQueue(
                    getMoviesByPhraseRequest(Fragmentator4000.encodeValue(p0.toString()))
                )
            searchView.clearFocus()
            return true
        }
    }
}