package com.example.springfragmenterclient.dataSources

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.springfragmenterclient.model.Line

class LineDataSourceFactory(private val phrase: String,
                            private val title: String?,
                            private val onError: (Throwable) -> Unit) : DataSource.Factory<Long, Line>() {
    val lineLiveData: MutableLiveData<PageKeyedDataSource<Long, Line>> by lazy {
        MutableLiveData<PageKeyedDataSource<Long, Line>>()
    }

    override fun create(): DataSource<Long, Line> {
        val lineDataSource = LineDataSource(phrase, title, onError)
        lineLiveData.postValue(lineDataSource)
        return lineDataSource
    }
}