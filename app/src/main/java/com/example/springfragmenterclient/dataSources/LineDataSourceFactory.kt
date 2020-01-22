package com.example.springfragmenterclient.dataSources

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.rest.ApiService
import javax.inject.Inject

class LineDataSourceFactory @Inject constructor(
    private val apiService: ApiService,
    private val application: Application
) : DataSource.Factory<Long, Line>() {
    val lineLiveData: MutableLiveData<PageKeyedDataSource<Long, Line>> by lazy {
        MutableLiveData<PageKeyedDataSource<Long, Line>>()
    }

    var phrase: String = ""
    var title: String? = null

    override fun create(): DataSource<Long, Line> {
        val lineDataSource = LineDataSource(phrase, title, apiService, application)
        lineLiveData.postValue(lineDataSource)
        return lineDataSource
    }
}