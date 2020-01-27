package com.example.springfragmenterclient.component

import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.activities.FragmentRequestActivity
import com.example.springfragmenterclient.activities.SelectedLineActivity
import com.example.springfragmenterclient.activities.SelectedMovieActivity
import com.example.springfragmenterclient.dataSources.LineDataSourceFactory
import com.example.springfragmenterclient.fragments.SearchMovie
import com.example.springfragmenterclient.fragments.SearchPhrase
import com.example.springfragmenterclient.modules.*
import com.example.springfragmenterclient.repositories.*
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        ViewModelModule::class,
        AppModule::class,
        AndroidSupportInjectionModule::class,
        AndroidInjectionModule::class,
        FragmentModule::class,
        ActivityModule::class
    ]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

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

    fun inject(fragmentator4000: Fragmentator4000)
}