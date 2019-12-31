package com.example.springfragmenterclient.rest

import com.example.springfragmenterclient.Fragmentator4000
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = Fragmentator4000.apiUrl+"/"

    private val okHttpClient = OkHttpClient()

    val gson: Gson = GsonBuilder()
        .setLenient()
        .disableHtmlEscaping()
        .create()

    private val gsonConverter: GsonConverterFactory by lazy {
        GsonConverterFactory
            .create(gson)
    }

    val INSTANCE: ApiService by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverter)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }
}