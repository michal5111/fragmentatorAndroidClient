package com.example.springfragmenterclient.fragments

import android.database.MatrixCursor
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
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

    private val _phraseLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    private val _titleLiveData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val phraseLiveData: LiveData<String>
        get() = _phraseLiveData

    val titleLiveData: LiveData<String>
        get() = _titleLiveData

    var linePagedList: LiveData<PagedList<Line>>? = null

    val liveDataSource: LiveData<LineDataSource>
        get() = lineDataSourceFactory.lineLiveData
    val compositeDisposable = CompositeDisposable()
    private val config: PagedList.Config = PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(LineDataSource.PAGE_SIZE)
        .build()

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

    private fun createLiveData() {
        this.phraseLiveData.value?.let {
            lineDataSourceFactory.apply {
                this.title = titleLiveData.value
                this.phrase = it
            }
            linePagedList = LivePagedListBuilder(lineDataSourceFactory, config).build()
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }

    fun setTitle(title: String) {
        _titleLiveData.value = title
        createLiveData()
    }

    fun setPhrase(phrase: String) {
        _phraseLiveData.value = phrase
        createLiveData()
    }
}
