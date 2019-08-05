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
import com.example.springfragmenterclient.*
import com.example.springfragmenterclient.Entities.Movie
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.MovieRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieSuggestionsCursorAdapter
import com.example.springfragmenterclient.utils.RequestQueueSingleton
import com.google.gson.Gson

class SearchMovie : Fragment() {

    companion object {
        fun newInstance() = SearchMovie()
    }

    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private var hints: List<Movie> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_movie_fragment, container, false)
        progressBar = root.findViewById(R.id.progressBar3)
        recyclerView = root.findViewById(R.id.RecyclerView)
        val viewManager = LinearLayoutManager(context)
        recyclerView.layoutManager = viewManager
        searchView = root.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(onQueryTextListener)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchMovieViewModel::class.java)
            .apply { applicationContext = activity?.applicationContext as Fragmentator4000 }
    }

    private fun getMoviesByTitleRequest(title: String) = JsonArrayRequest(
            Request.Method.GET, "${Fragmentator4000.apiUrl}/searchMovie?title=$title", null,
            Response.Listener { response ->
                val gson = Gson()
                viewModel.movies = gson.fromJson(response.toString(), Fragmentator4000.movieListType)
                recyclerView.adapter =
                    MovieRecyclerViewAdapter(viewModel.movies)
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

    private fun getHints(title: String) = JsonArrayRequest(
        Request.Method.GET, "${Fragmentator4000.apiUrl}/movieHints?title=$title", null,
        Response.Listener { response ->
            val gson = Gson()
            hints = gson.fromJson(response.toString(), Fragmentator4000.movieListType)
            val cursor = MatrixCursor(arrayOf("_id","hint"))
            hints.forEach { hint ->
                val rowBuilder = cursor.newRow()
                rowBuilder.apply {
                    add("_id", hint.id)
                    add("hint",hint.fileName)
                }
            }
            searchView.suggestionsAdapter = MovieSuggestionsCursorAdapter(
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
        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            RequestQueueSingleton.getInstance(context!!)
                .addToRequestQueue(
                    getMoviesByTitleRequest(Fragmentator4000.encodeValue(p0.toString()))
                )
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            RequestQueueSingleton.getInstance(context!!)
                .addToRequestQueue(
                    getHints(Fragmentator4000.encodeValue(p0.toString()))
                )
            return false
        }
    }
}