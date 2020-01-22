package com.example.springfragmenterclient.dataSources

import android.annotation.SuppressLint
import android.app.Application
import androidx.paging.PageKeyedDataSource
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.rest.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class LineDataSource(
    private val phrase: String,
    private val title: String?,
    private val apiService: ApiService,
    private val application: Application
) : PageKeyedDataSource<Long, Line>() {

    companion object {
        const val PAGE_SIZE = 50
        const val FIRST_PAGE = 0
    }

    private fun searchPhrase(phrase: String, page: Number) =
        apiService.searchPhrase(phrase, page, PAGE_SIZE, title)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Line>
    ) {
        searchPhrase(phrase, FIRST_PAGE)
            .subscribeBy(
                onNext = { callback.onResult(it.content, null, FIRST_PAGE + 1L) },
                onError = (application as Fragmentator4000)::errorHandler
            )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Line>) {
        searchPhrase(phrase, params.key)
            .subscribeBy(
                onNext = {
                    val key = if (it.last) null else params.key + 1
                    if (key != null) {
                        callback.onResult(it.content, key)
                    }
                },
                onError = (application as Fragmentator4000)::errorHandler
            )
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Line>) {
        searchPhrase(phrase, params.key)
            .subscribeBy(
                onNext = {
                    val key = if (params.key > 1) params.key - 1 else null
                    if (key != null) {
                        callback.onResult(it.content, key)
                    }
                },
                onError = (application as Fragmentator4000)::errorHandler
            )
    }
}