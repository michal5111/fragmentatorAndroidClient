package com.example.springfragmenterclient.repositories

import com.example.springfragmenterclient.entities.FragmentRequest
import com.example.springfragmenterclient.rest.ApiService
import com.example.springfragmenterclient.rest.RetrofitClient
import com.example.springfragmenterclient.rest.responses.ConversionStatus
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okio.BufferedSource

class FragmentRequestRepository {

    private val apiService: ApiService = RetrofitClient.INSTANCE

    fun save(fragmentRequest: FragmentRequest): Single<FragmentRequest> =
        apiService.postFragmentRequest(fragmentRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun fragmentRequest(id: Long): Observable<ConversionStatus> =
        apiService.requestFragment(id)
            .subscribeOn(Schedulers.io())
            .flatMap { responseBody -> events(responseBody.source()) }
            .map { line -> RetrofitClient.gson.fromJson(line, ConversionStatus::class.java) }

    private fun events(source: BufferedSource): Observable<String> = Observable.create { subscriber ->
        while (!source.exhausted()) {
            try {
                subscriber.onNext(source.readUtf8Line()!!)
            } catch (e: Exception) {
                subscriber.onError(e)
            }
        }
        subscriber.onComplete()
    }
}