package com.example.springfragmenterclient.dataSources

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.springfragmenterclient.entities.Line

class LineDataSourceFactory(private val phrase: String) : DataSource.Factory<Long, Line>() {
    val lineLiveData: MutableLiveData<PageKeyedDataSource<Long, Line>> by lazy {
        MutableLiveData<PageKeyedDataSource<Long, Line>>()
    }

    override fun create(): DataSource<Long, Line> {
        val lineDataSource = LineDataSource(phrase)
        lineLiveData.postValue(lineDataSource)
        return lineDataSource
    }
}