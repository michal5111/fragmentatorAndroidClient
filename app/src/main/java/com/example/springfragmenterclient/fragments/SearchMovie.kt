package com.example.springfragmenterclient.fragments

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
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.MovieRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieSuggestionsCursorAdapter
import com.example.springfragmenterclient.utils.RequestQueueSingleton

class SearchMovie : Fragment() {

    companion object {
        fun newInstance() = SearchMovie()
    }

    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var requestQueue: RequestQueueSingleton

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
        requestQueue = RequestQueueSingleton.getInstance(context!!)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchMovieViewModel::class.java)
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            requestQueue.addToRequestQueue(
                viewModel.getMoviesByTitleRequest(Fragmentator4000.encodeValue(p0.toString()),
                    {
                        recyclerView.adapter = MovieRecyclerViewAdapter(viewModel.movies)
                        progressBar.visibility = View.INVISIBLE
                    },
                    { error ->
                        Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE

                    }
                )
            )
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            requestQueue.addToRequestQueue(
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()),
                    { cursor ->
                        searchView.suggestionsAdapter = MovieSuggestionsCursorAdapter(
                            context!!,
                            cursor,
                            true,
                            searchView
                        )
                    },
                    { error ->
                        Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                    })
            )
            return false
        }
    }
}
