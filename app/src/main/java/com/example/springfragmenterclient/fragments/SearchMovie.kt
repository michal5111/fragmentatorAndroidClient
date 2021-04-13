package com.example.springfragmenterclient.fragments

import android.annotation.SuppressLint
import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.MovieRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieSuggestionsCursorAdapter
import com.example.springfragmenterclient.model.Movie
import dagger.android.support.DaggerFragment
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchMovie : DaggerFragment() {

    companion object {
        fun newInstance() = SearchMovie()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView

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
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchMovieViewModel::class.java]
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        @SuppressLint("CheckResult")
        override fun onQueryTextSubmit(query: String?): Boolean {
            val queryText = query ?: ""
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            viewModel.compositeDisposable +=
                viewModel.getMovies(Fragmentator4000.encodeValue(queryText))
                    .doOnSubscribe { progressBar.visibility = View.VISIBLE }
                    .doFinally { progressBar.visibility = View.INVISIBLE }
                    .subscribeBy(
                        onNext = this@SearchMovie::showMovies,
                        onError = (requireActivity().application as Fragmentator4000)::errorHandler
                    )

            return true
        }

        @SuppressLint("CheckResult")
        override fun onQueryTextChange(query: String?): Boolean {
            val queryText = query ?: ""
            viewModel.compositeDisposable +=
                viewModel.getHints(Fragmentator4000.encodeValue(queryText))
                    .subscribeBy(
                        onNext = this@SearchMovie::createAdapter,
                        onError = (requireActivity().application as Fragmentator4000)::errorHandler
                    )
            return false
        }
    }

    fun showMovies(movies: List<Movie>) {
        recyclerView.adapter = MovieRecyclerViewAdapter(movies)
    }

    fun createAdapter(cursor: MatrixCursor) {
        searchView.suggestionsAdapter = MovieSuggestionsCursorAdapter(
            requireContext(),
            cursor,
            true,
            searchView
        )
    }
}
