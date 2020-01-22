package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.rest.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MovieRepository @Inject constructor(private val apiService: ApiService) {

    fun getLines(id: Long) =
        apiService.getLines(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}