package com.example.springfragmenterclient.fragments

import android.annotation.SuppressLint
import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.MovieRecyclerViewAdapter
import com.example.springfragmenterclient.adapters.MovieSuggestionsCursorAdapter
import com.example.springfragmenterclient.model.Movie
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchMovie : Fragment() {

    companion object {
        fun newInstance() = SearchMovie()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: SearchMovieViewModel
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
        (activity!!.application as Fragmentator4000).appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[SearchMovieViewModel::class.java]
    }

    private val onQueryTextListener = object : SearchView.OnQueryTextListener {
        @SuppressLint("CheckResult")
        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            viewModel.compositeDisposable +=
                viewModel.getMovies(Fragmentator4000.encodeValue(p0.toString()))
                    .doOnSubscribe { progressBar.visibility = View.VISIBLE }
                    .doFinally { progressBar.visibility = View.INVISIBLE }
                    .subscribeBy(
                        onNext = this@SearchMovie::showMovies,
                        onError = (activity!!.application as Fragmentator4000)::errorHandler
                    )

            return true
        }

        @SuppressLint("CheckResult")
        override fun onQueryTextChange(p0: String?): Boolean {
            viewModel.compositeDisposable +=
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()))
                    .subscribeBy(
                        onNext = { createAdapter(it) },
                        onError = (activity!!.application as Fragmentator4000)::errorHandler
                    )
            return false
        }
    }

    fun showMovies(movies: List<Movie>) {
        recyclerView.adapter = MovieRecyclerViewAdapter(movies)
    }

    fun createAdapter(cursor: MatrixCursor) {
        searchView.suggestionsAdapter = MovieSuggestionsCursorAdapter(
            context!!,
            cursor,
            true,
            searchView
        )
    }
}
