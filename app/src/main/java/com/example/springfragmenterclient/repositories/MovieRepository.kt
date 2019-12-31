package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.rest.ApiService
import com.example.springfragmenterclient.rest.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MovieRepository {

    private val apiService: ApiService = RetrofitClient.INSTANCE


    fun getLines(id: Long) =
        apiService.getLines(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}