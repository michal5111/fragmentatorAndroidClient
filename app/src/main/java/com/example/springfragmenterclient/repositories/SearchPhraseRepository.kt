package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.rest.ApiService
import com.example.springfragmenterclient.rest.RetrofitClient
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SearchPhraseRepository {

    private val apiService: ApiService = RetrofitClient.INSTANCE

    private val PAGE_SIZE = 20

    fun getHints(phrase: String): Flowable<List<Line>> =
        apiService.lineHints(phrase)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun searchPhrase(phrase: String, page: Number) =
        apiService.searchPhrase(phrase, page, PAGE_SIZE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}