package com.example.springfragmenterclient.fragments

import android.annotation.SuppressLint
import android.database.MatrixCursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.activities.MainActivity
import com.example.springfragmenterclient.adapters.LineSuggestionsCursorAdapter
import com.example.springfragmenterclient.adapters.LineWithMovieTitleRecyclerViewAdapter
import com.example.springfragmenterclient.dataSources.LineDataSource
import dagger.android.support.DaggerFragment
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SearchPhrase : DaggerFragment() {

    companion object {
        fun newInstance() = SearchPhrase()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: SearchPhraseViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchView: SearchView
    private lateinit var filterSearchView: SearchView
    private lateinit var lineAdapter: LineWithMovieTitleRecyclerViewAdapter
    private val dataSourceObserver: Observer<LineDataSource> = Observer {
        it.resultSizeLiveData.observe(viewLifecycleOwner, resultSizeObserver)
    }
    private val resultSizeObserver: Observer<Int> = Observer {
        filterSearchView.visibility =
            if (it > 0 || filterSearchView.query.isNotEmpty()) View.VISIBLE else View.INVISIBLE
    }

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
        viewModel = ViewModelProvider(this, viewModelFactory)[SearchPhraseViewModel::class.java]
        lineAdapter = LineWithMovieTitleRecyclerViewAdapter()
        recyclerView.adapter = lineAdapter
        viewModel.liveDataSource.observe(viewLifecycleOwner, dataSourceObserver)
    }

    private fun setObserver() {
        viewModel.linePagedList?.observe(this) {
            lineAdapter.submitList(it)
            progressBar.visibility = View.INVISIBLE
        }
    }


    private val onSearchQueryTextListener = object : SearchView.OnQueryTextListener {
        @SuppressLint("CheckResult")
        override fun onQueryTextChange(p0: String?): Boolean {
            viewModel.compositeDisposable.dispose()
            viewModel.compositeDisposable +=
                viewModel.getHints(Fragmentator4000.encodeValue(p0.toString()))
                    .subscribeBy(
                        onNext = this@SearchPhrase::createAdapter,
                        onError = (requireActivity().application as Fragmentator4000)::errorHandler
                    )
            return false
        }

        override fun onQueryTextSubmit(query: String?): Boolean {
            val queryText = query ?: ""
            Fragmentator4000.hideKeyboard(activity as MainActivity)
            progressBar.visibility = View.VISIBLE
            viewModel.setPhrase(queryText)
            setObserver()
            searchView.clearFocus()
            return true
        }
    }

    private fun createAdapter(cursor: MatrixCursor) {
        searchView.suggestionsAdapter = LineSuggestionsCursorAdapter(
            requireContext(),
            cursor,
            true,
            searchView
        )
    }


    private val onFilterQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return false
        }

        override fun onQueryTextChange(title: String?): Boolean {
            val titleText = title ?: ""
            viewModel.setTitle(titleText)
            setObserver()
            return false
        }

    }
}