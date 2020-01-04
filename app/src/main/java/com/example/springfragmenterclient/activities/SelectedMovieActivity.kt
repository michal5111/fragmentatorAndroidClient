package com.example.springfragmenterclient.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.R
import com.example.springfragmenterclient.adapters.DialogLineRecyclerViewAdapter
import com.example.springfragmenterclient.entities.Movie
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class SelectedMovieActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var selectButton: Button
    private lateinit var filterSearchView: SearchView
    private lateinit var viewModel: SelectedMovieViewModel
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selected_movie)
        viewModel = ViewModelProviders.of(this)[SelectedMovieViewModel::class.java]
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
        compositeDisposable += viewModel.getLines(viewModel.selectedMovie.id!!)
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
        if (adapter.selectedItems.size() >= 2) {
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
                    notifyDataSetChanged()
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

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}
