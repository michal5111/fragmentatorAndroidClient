package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.rest.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class LineRepository @Inject constructor(private val apiService: ApiService) {

    fun getLineSnapshot(id: Long) =
        apiService.getLineSnapshot(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.url }
}