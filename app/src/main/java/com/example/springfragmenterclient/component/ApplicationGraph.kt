package com.example.springfragmenterclient.component

import com.example.springfragmenterclient.activities.FragmentRequestActivity
import com.example.springfragmenterclient.activities.SelectedLineActivity
import com.example.springfragmenterclient.activities.SelectedMovieActivity
import com.example.springfragmenterclient.dataSources.LineDataSourceFactory
import com.example.springfragmenterclient.fragments.SearchMovie
import com.example.springfragmenterclient.fragments.SearchPhrase
import com.example.springfragmenterclient.modules.AppModule
import com.example.springfragmenterclient.modules.NetworkModule
import com.example.springfragmenterclient.modules.ViewModelModule
import com.example.springfragmenterclient.repositories.*
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ViewModelModule::class,
        AppModule::class
    ]
)
interface ApplicationGraph {

    fun fragmentRequestRepository(): FragmentRequestRepository

    fun lineRepository(): LineRepository

    fun movieRepository(): MovieRepository

    fun searchMovieRepository(): SearchMovieRepository

    fun searchPhraseRepository(): SearchPhraseRepository

    fun lineDataSourceFactory(): LineDataSourceFactory

    fun inject(fragmentRequestActivity: FragmentRequestActivity)

    fun inject(selectedLineActivity: SelectedLineActivity)

    fun inject(selectedMovieActivity: SelectedMovieActivity)

    fun inject(searchPhrase: SearchPhrase)

    fun inject(searchMovie: SearchMovie)
}