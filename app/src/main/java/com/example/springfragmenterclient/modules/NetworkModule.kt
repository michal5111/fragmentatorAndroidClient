package com.example.springfragmenterclient.modules

import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.rest.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object NetworkModule {


    private const val BASE_URL = Fragmentator4000.apiUrl + "/"


    @Provides
    @Reusable
    @JvmStatic
    internal fun providesOkHttpClient() = OkHttpClient.Builder()
        .readTimeout(1, TimeUnit.MINUTES)
        .build()

    @Provides
    @Reusable
    @JvmStatic
    internal fun providesGson() = GsonBuilder()
        .setLenient()
        .disableHtmlEscaping()
        .create()

    @Provides
    @Reusable
    @JvmStatic
    internal fun providesGsonConverterFactory(gson: Gson) =
        GsonConverterFactory
            .create(gson)

    @Provides
    @Reusable
    @JvmStatic
    internal fun providesRetrofitInterface(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
        .client(okHttpClient)
        .build()

    @Provides
    @Reusable
    @JvmStatic
    internal fun providesApi(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

}