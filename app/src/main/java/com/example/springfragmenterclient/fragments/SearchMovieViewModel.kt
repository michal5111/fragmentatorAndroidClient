package com.example.springfragmenterclient.fragments

import android.database.MatrixCursor
import androidx.lifecycle.ViewModel
import com.example.springfragmenterclient.entities.Movie
import com.example.springfragmenterclient.repositories.SearchMovieRepository
import io.reactivex.Flowable

class SearchMovieViewModel : ViewModel() {

    private val searchMovieRepository = SearchMovieRepository()

    fun getMovies(title: String): Flowable<List<Movie>> {
        return searchMovieRepository.getMoviesByTitle(title)
    }

    fun getHints(text: String): Flowable<MatrixCursor> {
        return searchMovieRepository.getHints(text)
    }
}
