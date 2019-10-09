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
import com.example.springfragmenterclient.adapters.LineSuggestionsCursorAdapter
import com.example.springfragmenterclient.adapters.LineWithMovieTitleRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieWithLinesRecyclerViewAdapter
import com.example.springfragmenterclient.utils.RequestQueueSingleton

class SearchPhrase : Fragment() {

    companion object {
        fun newInstance() = SearchPhrase()
    }

    private lateinit var viewModel: SearchPhraseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var filterSearchView: SearchView
    private lateinit var requestQueue: RequestQueueSingleton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_phrase_fragment, container, false)
        searchView = root.findViewById(R.id.searchView)
        progressBar = root.findViewById(R.id.progressBar3)
        recyclerView = root.findViewById(R.id.RecyclerView)
        filterSearchView = root.findViewById(R.id.filterSearchView)
        filterSearchView.setOnQueryTextListener(onFilterQueryTextListener)
        val viewManager = LinearLayoutManager(context)
        recyclerView.layoutManager = viewManager
        searchView.setOnQueryTextListener(onSearchQueryTextListener)
        requestQueue = RequestQueueSingleton.getInstance(context!!)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchPhraseViewModel::class.java)
    }


    private val onSearchQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(p0: String?): Boolean {
            requestQueue.addToRequestQueue(
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()),
                    { cursor ->
                        searchView.suggestionsAdapter = LineSuggestionsCursorAdapter(
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

        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            requestQueue.addToRequestQueue(
                viewModel.getMoviesByPhraseRequest(Fragmentator4000.encodeValue(p0.toString()),
                    {
                        recyclerView.adapter = LineWithMovieTitleRecyclerViewAdapter(
                            viewModel.lines
                        )
                        progressBar.visibility = View.INVISIBLE
                    },
                    { error ->
                        Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.INVISIBLE
                    })
            )
            searchView.clearFocus()
            filterSearchView.visibility = View.VISIBLE
            return true
        }
    }

    private val onFilterQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            (recyclerView.adapter as LineWithMovieTitleRecyclerViewAdapter).filter.filter(p0)
            return false
        }

    }
}