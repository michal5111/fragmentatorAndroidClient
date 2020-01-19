package com.example.springfragmenterclient.rest

import com.example.springfragmenterclient.model.FragmentRequest
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.model.Response
import com.example.springfragmenterclient.model.page.Page
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    @GET("fragmentRequest/{id}")
    @Streaming
    fun requestFragment(@Path("id") id: Long): Observable<ResponseBody>

    @POST("fragmentRequest")
    fun postFragmentRequest(@Body fragmentRequest: FragmentRequest): Single<FragmentRequest>

    @GET("searchPhrase")
    fun searchPhrase(@Query("phrase") phrase: String,
                     @Query("page") page: Number,
                     @Query("size") size: Number,
                     @Query("title") title: String?
    ): Flowable<Page<Line>>

    @GET("searchMovie")
    fun searchMovie(@Query("title") title: String): Flowable<List<Movie>>

    @GET("movieHints")
    fun movieHints(@Query("title") title: String): Flowable<List<Movie>>

    @GET("lineHints")
    fun lineHints(@Query("phrase") phrase: String): Flowable<List<Line>>

    @GET("lineSnapshot")
    fun getLineSnapshot(@Query("lineId") id: Long): Single<Response>

    @GET("lines")
    fun getLines(@Query("movieId") id: Long): Flowable<List<Line>>
}