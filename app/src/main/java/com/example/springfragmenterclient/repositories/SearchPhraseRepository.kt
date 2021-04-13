package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.rest.ApiService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchPhraseRepository @Inject constructor(private val apiService: ApiService) {

    fun getHints(phrase: String): Flowable<List<Line>> =
        apiService.lineHints(phrase)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

//    fun searchPhrase(phrase: String, page: Number) =
//        apiService.searchPhrase(phrase, page, PAGE_SIZE, )
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
}