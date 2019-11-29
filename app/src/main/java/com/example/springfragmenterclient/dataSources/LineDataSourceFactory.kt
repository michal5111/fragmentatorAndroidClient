package com.example.springfragmenterclient.dataSources

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.example.springfragmenterclient.entities.Line

class LineDataSourceFactory(private val phrase: String, private val context: Context) : DataSource.Factory<Long, Line>() {
    val lineLiveData: MutableLiveData<PageKeyedDataSource<Long, Line>> by lazy {
        MutableLiveData<PageKeyedDataSource<Long, Line>>()
    }

    override fun create(): DataSource<Long, Line> {
        val lineDataSource = LineDataSource(phrase, context)
        lineLiveData.postValue(lineDataSource)
        return lineDataSource
    }
}