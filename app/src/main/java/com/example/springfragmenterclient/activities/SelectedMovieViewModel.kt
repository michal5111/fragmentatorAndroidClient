package com.example.springfragmenterclient.activities

import androidx.lifecycle.ViewModel
import com.example.springfragmenterclient.adapters.DialogLineRecyclerViewAdapter
import com.example.springfragmenterclient.model.FragmentRequest
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.repositories.MovieRepository
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SelectedMovieViewModel
@Inject constructor(private val movieRepository: MovieRepository) : ViewModel() {

    lateinit var selectedMovie: Movie
    lateinit var lines: List<Line>
    var fragmentRequest: FragmentRequest = FragmentRequest()
    val compositeDisposable = CompositeDisposable()

    fun getLines(id: Long): Flowable<List<Line>> = movieRepository.getLines(id)
        .doOnNext {
            lines = it
        }

    fun onLinesSelected(adapter: DialogLineRecyclerViewAdapter) {
        val list = lines.filter {
            adapter.selectedItems.get(lines.indexOf(it), false)
        }.toList()
        fragmentRequest.apply {
            startLineId = list.first().id
            stopLineId = list.last().id
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}