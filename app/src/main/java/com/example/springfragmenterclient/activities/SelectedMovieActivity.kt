package com.example.springfragmenterclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.DialogLineRecyclerViewAdapter
import com.example.springfragmenterclient.model.Movie
import dagger.android.support.DaggerAppCompatActivity
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class SelectedMovieActivity : DaggerAppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var selectButton: Button
    private lateinit var filterSearchView: SearchView
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var viewModel: SelectedMovieViewModel

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_movie)
        viewModel =
            ViewModelProvider(this, viewModelFactory)[SelectedMovieViewModel::class.java]
        viewModel.selectedMovie = intent.getSerializableExtra("SELECTED_MOVIE") as Movie
        recyclerView = findViewById(R.id.RecyclerView)
        selectButton = findViewById(R.id.button)
        viewModel.fragmentRequest.apply {
            movieId = viewModel.selectedMovie.id
        }
        val viewManager = LinearLayoutManager(this)
        val movieTitleTextView: TextView = findViewById(R.id.movieTitle)
        movieTitleTextView.text = viewModel.selectedMovie.fileName
        recyclerView.layoutManager = viewManager
        viewModel.compositeDisposable += viewModel.getLines(viewModel.selectedMovie.id!!)
            .subscribeBy(
                onNext = { onResponseListener() },
                onError = (application as Fragmentator4000)::errorHandler
            )
        selectButton.setOnClickListener {
            val intent = Intent(applicationContext, FragmentRequestActivity::class.java).apply {
                putExtra("SELECTED_MOVIE", viewModel.selectedMovie)
                putExtra("FRAGMENT_REQUEST", viewModel.fragmentRequest)
            }
            startActivity(intent)
        }
        filterSearchView = findViewById(R.id.filterSearchView)
        filterSearchView.setOnQueryTextListener(onFilterQueryTextListener)
    }

    private fun onLinesSelectedListener(adapter: DialogLineRecyclerViewAdapter) {
        if (filterSearchView.query.isNotBlank()) {
            filterSearchView.setQuery("", false)
            //recyclerView.scrollTo()
        }
        if (adapter.selectedItems.size() >= 1) {
            selectButton.isEnabled = true
            viewModel.onLinesSelected(adapter)
        } else {
            selectButton.isEnabled = false
        }
    }

    private fun onResponseListener() {
        recyclerView.adapter = DialogLineRecyclerViewAdapter(viewModel.lines).apply {
            setOnLinesSelectedListener { adapter -> onLinesSelectedListener(adapter) }
        }
        if (intent.hasExtra("POSITION")) {
            val position: Int = intent.getIntExtra("POSITION", 0)
            recyclerView.apply {
                scrollToPosition(position)
                (adapter as DialogLineRecyclerViewAdapter).apply {
                    selectedItems.put(position, true)
                    notifyItemChanged(position)
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
