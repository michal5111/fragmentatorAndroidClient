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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.LineSuggestionsCursorAdapter
import com.example.springfragmenterclient.adapters.LineWithMovieTitleRecyclerViewAdapter
import com.example.springfragmenterclient.model.Line
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy

class SearchPhrase : Fragment() {

    companion object {
        fun newInstance() = SearchPhrase()
    }

    private lateinit var viewModel: SearchPhraseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var filterSearchView: SearchView
    private lateinit var lineAdapter: LineWithMovieTitleRecyclerViewAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.search_phrase_fragment, container, false)
        searchView = root.findViewById(R.id.searchView)
        progressBar = root.findViewById(R.id.progressBar3)
        recyclerView = root.findViewById(R.id.RecyclerView)
        filterSearchView = root.findViewById(R.id.filterSearchView)
        val viewManager = LinearLayoutManager(context)
        recyclerView.layoutManager = viewManager
        recyclerView.setHasFixedSize(true)
        searchView.setOnQueryTextListener(onSearchQueryTextListener)
        filterSearchView.setOnQueryTextListener(onFilterQueryTextListener)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchPhraseViewModel::class.java)
        lineAdapter = LineWithMovieTitleRecyclerViewAdapter()
        recyclerView.adapter = lineAdapter
    }

    private fun setObserver() {
        viewModel.linePagedList.observe(this,
            Observer<PagedList<Line>> {
                    t -> lineAdapter.submitList(t)
                    progressBar.visibility = View.INVISIBLE
            })
    }



    private val onSearchQueryTextListener = object : SearchView.OnQueryTextListener {
        @SuppressLint("CheckResult")
        override fun onQueryTextChange(p0: String?): Boolean {
            compositeDisposable.clear()
            compositeDisposable.add(
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()))
                    .subscribeBy(
                        onNext = {createAdapter(it)},
                        onError = {showError(it)}
                    )
            )
            return false
        }

        override fun onQueryTextSubmit(p0: String?): Boolean {
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            viewModel.phrase = p0.toString()
            viewModel.createLiveData(viewModel.phrase, viewModel.title)
            setObserver()
            searchView.clearFocus()
            filterSearchView.visibility = View.VISIBLE
            return true
        }
    }

    private fun createAdapter(cursor: MatrixCursor) {
        searchView.suggestionsAdapter = LineSuggestionsCursorAdapter(
            context!!,
            cursor,
            true,
            searchView
        )
    }

    private fun showError(error: Throwable) {
        Toast.makeText(context, "error " + error.localizedMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private val onFilterQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            viewModel.title = p0.toString()
            viewModel.createLiveData(viewModel.phrase, viewModel.title)
            setObserver()
            return false
        }

    }
}