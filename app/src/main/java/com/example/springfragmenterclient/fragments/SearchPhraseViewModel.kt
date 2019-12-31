package com.example.springfragmenterclient.fragments

import android.app.Application
import android.database.MatrixCursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.springfragmenterclient.dataSources.LineDataSource
import com.example.springfragmenterclient.dataSources.LineDataSourceFactory
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.repositories.SearchPhraseRepository

class SearchPhraseViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var linePagedList: LiveData<PagedList<Line>>
    private lateinit var liveDataSource: LiveData<PageKeyedDataSource<Long, Line>>

    private val searchPhraseRepository = SearchPhraseRepository()

    fun getHints(phrase: String) =
        searchPhraseRepository.getHints(phrase)
            .map {
                val cursor = MatrixCursor(arrayOf("_id", "hint"))
                it.forEach { hint ->
                    val rowBuilder = cursor.newRow()
                    rowBuilder.apply {
                        add("_id", hint.id)
                        add("hint", hint.textLines)
                    }
                }
                return@map cursor
            }

    fun createLiveData(phrase: String) {
        val lineDataSourceFactory = LineDataSourceFactory(phrase)
        liveDataSource = lineDataSourceFactory.lineLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(LineDataSource.PAGE_SIZE)
            .build()

        linePagedList = LivePagedListBuilder(lineDataSourceFactory, config).build()
    }
}
