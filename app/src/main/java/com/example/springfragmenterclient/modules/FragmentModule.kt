package com.example.springfragmenterclient.modules

import com.example.springfragmenterclient.fragments.SearchMovie
import com.example.springfragmenterclient.fragments.SearchPhrase
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributesSearchPhraseFragment(): SearchPhrase

    @ContributesAndroidInjector
    abstract fun contributesSearchMovieFragment(): SearchMovie
}