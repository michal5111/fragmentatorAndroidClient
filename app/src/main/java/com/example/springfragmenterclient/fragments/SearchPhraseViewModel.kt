package com.example.springfragmenterclient.fragments

import android.app.Application
import android.database.Cursor
import android.database.MatrixCursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagedList
import com.android.volley.Response
import com.android.volley.VolleyError
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.dataSources.LineDataSource
import com.example.springfragmenterclient.dataSources.LineDataSourceFactory
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.utils.GsonRequest

class SearchPhraseViewModel(application: Application) : AndroidViewModel(application) {
//    var lines: List<Line> = emptyList()

    lateinit var linePagedList: LiveData<PagedList<Line>>
    private lateinit var liveDataSource: LiveData<PageKeyedDataSource<Long, Line>>

    fun getHints(phrase: String, successListener: (Cursor) -> Unit, errorListener: (VolleyError) -> Unit) =
        GsonRequest<List<Line>>(
            "${Fragmentator4000.apiUrl}/lineHints?phrase=$phrase",
            Fragmentator4000.linesListType,
            mutableMapOf(),
            Response.Listener { response ->
                val hints: List<Line> = response
                val cursor = MatrixCursor(arrayOf("_id", "hint"))
                hints.forEach { hint ->
                    val rowBuilder = cursor.newRow()
                    rowBuilder.apply {
                        add("_id", hint.id)
                        add("hint", hint.textLines)
                    }
                }
                successListener.invoke(cursor)
            },
            Response.ErrorListener { error ->
                errorListener.invoke(error)
            }
        )

    fun createLiveData(phrase: String) {
        val lineDataSourceFactory = LineDataSourceFactory(phrase, getApplication())
        liveDataSource = lineDataSourceFactory.lineLiveData

        val config: PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(LineDataSource.PAGE_SIZE)
            .build()

        linePagedList = LivePagedListBuilder(lineDataSourceFactory, config).build()
    }
}
