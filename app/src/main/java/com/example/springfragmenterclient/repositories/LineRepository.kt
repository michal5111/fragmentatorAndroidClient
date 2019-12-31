package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.rest.ApiService
import com.example.springfragmenterclient.rest.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LineRepository {

    private val apiService: ApiService = RetrofitClient.INSTANCE

    fun getLineSnapshot(id: Long) =
        apiService.getLineSnapshot(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.url }
}