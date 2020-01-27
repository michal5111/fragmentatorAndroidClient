package com.example.springfragmenterclient.modules

import com.example.springfragmenterclient.activities.FragmentRequestActivity
import com.example.springfragmenterclient.activities.SelectedLineActivity
import com.example.springfragmenterclient.activities.SelectedMovieActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributesFragmentRequestActivity(): FragmentRequestActivity

    @ContributesAndroidInjector
    abstract fun contributesSelectedLineActivity(): SelectedLineActivity

    @ContributesAndroidInjector
    abstract fun contributesSelectedMovieActivity(): SelectedMovieActivity
}