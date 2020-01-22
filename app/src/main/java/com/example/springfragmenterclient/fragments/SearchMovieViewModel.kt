package com.example.springfragmenterclient.fragments

import android.database.MatrixCursor
import androidx.lifecycle.ViewModel
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.repositories.SearchMovieRepository
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchMovieViewModel @Inject constructor(private val searchMovieRepository: SearchMovieRepository) :
    ViewModel() {

    val compositeDisposable = CompositeDisposable()

    fun getMovies(title: String): Flowable<List<Movie>> {
        return searchMovieRepository.getMoviesByTitle(title)
    }

    fun getHints(text: String): Flowable<MatrixCursor> {
        return searchMovieRepository.getHints(text)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
