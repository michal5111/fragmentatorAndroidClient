package com.example.springfragmenterclient.fragments

import android.database.MatrixCursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.example.springfragmenterclient.dataSources.LineDataSource
import com.example.springfragmenterclient.dataSources.LineDataSourceFactory
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.repositories.SearchPhraseRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SearchPhraseViewModel @Inject constructor(
    private val searchPhraseRepository: SearchPhraseRepository,
    private val lineDataSourceFactory: LineDataSourceFactory
) : ViewModel() {

    lateinit var linePagedList: LiveData<PagedList<Line>>
    private var liveDataSource: LiveData<PageKeyedDataSource<Long, Line>> =
        lineDataSourceFactory.lineLiveData
    val compositeDisposable = CompositeDisposable()
    private val config: PagedList.Config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(LineDataSource.PAGE_SIZE)
        .build()

    var title: String? = null
    var phrase: String = ""

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

    fun createLiveData(phrase: String, title: String?) {
        lineDataSourceFactory.apply {
            this.title = title
            this.phrase = phrase
        }
        linePagedList = LivePagedListBuilder(lineDataSourceFactory, config).build()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
