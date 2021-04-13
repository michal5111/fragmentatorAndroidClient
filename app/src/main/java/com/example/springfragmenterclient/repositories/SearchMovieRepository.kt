package com.example.springfragmenterclient.repositories

import android.database.MatrixCursor
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.rest.ApiService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchMovieRepository @Inject constructor(private val apiService: ApiService) {

    fun getMoviesByTitle(title: String): Flowable<List<Movie>> =
        apiService.searchMovie(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun getHints(title: String): Flowable<MatrixCursor> =
        apiService.movieHints(title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                val cursor = MatrixCursor(arrayOf("_id", "hint"))
                it.forEach { hint ->
                    val rowBuilder = cursor.newRow()
                    rowBuilder.apply {
                        add("_id", hint.id)
                        add("hint", hint.fileName)
                    }
                }
                return@map cursor
            }
}