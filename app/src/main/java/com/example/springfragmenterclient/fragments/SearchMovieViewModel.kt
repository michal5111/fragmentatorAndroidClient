package com.example.springfragmenterclient.fragments

import android.content.Context
import androidx.lifecycle.ViewModel;
import com.example.springfragmenterclient.Entities.Movie

class SearchMovieViewModel : ViewModel() {
    lateinit var applicationContext: Context
    var movies: List<Movie> = emptyList()
}
