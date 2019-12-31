package com.example.springfragmenterclient.fragments

import android.annotation.SuppressLint
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
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.MovieRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieSuggestionsCursorAdapter
import com.example.springfragmenterclient.entities.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class SearchMovie : Fragment() {

    companion object {
        fun newInstance() = SearchMovie()
    }

    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private val compositeDisposable = CompositeDisposable()


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
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        @SuppressLint("CheckResult")
        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            compositeDisposable.add(
                viewModel.getMovies(Fragmentator4000.encodeValue(p0.toString()))
                    .doOnSubscribe{progressBar.visibility = View.VISIBLE}
                    .doFinally { progressBar.visibility = View.INVISIBLE }
                    .subscribeBy(
                        onNext = {showMovies(it)},
                        onError = {showError(it)}
                    )
            )
            return true
        }

        @SuppressLint("CheckResult")
        override fun onQueryTextChange(p0: String?): Boolean {
            compositeDisposable.add(
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()))
                    .subscribeBy(
                        onNext = {createAdapter(it)},
                        onError = {showError(it)}
                    )
            )
            return false
        }
    }

    fun showMovies(movies: List<Movie>) {
        recyclerView.adapter = MovieRecyclerViewAdapter(movies)
    }

    fun showError(error: Throwable) {
        Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    fun createAdapter(cursor: MatrixCursor) {
        searchView.suggestionsAdapter = MovieSuggestionsCursorAdapter(
            context!!,
            cursor,
            true,
            searchView
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
