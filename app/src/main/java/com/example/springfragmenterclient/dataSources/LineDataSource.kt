package com.example.springfragmenterclient.dataSources

import android.content.Context
import androidx.paging.PageKeyedDataSource
import com.android.volley.Response
import com.example.springfragmenterclient.Fragmentator4000
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.entities.page.Page
import com.example.springfragmenterclient.utils.GsonRequest
import com.example.springfragmenterclient.utils.RequestQueueSingleton

class LineDataSource(val phrase: String, private val context: Context) : PageKeyedDataSource<Long, Line>() {

    companion object {
        const val PAGE_SIZE = 50
        const val FIRST_PAGE = 0
    }

    private val requestQueue: RequestQueueSingleton = RequestQueueSingleton.getInstance(context)

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Long, Line>
    ) {
        requestQueue.addToRequestQueue(
            GsonRequest<Page<Line>>(
                "${Fragmentator4000.apiUrl}/searchPhrase?phrase=$phrase&page=$FIRST_PAGE&size=$PAGE_SIZE",
                Fragmentator4000.pageOfLinesType,
                mutableMapOf(),
                Response.Listener { response ->
                    callback.onResult(response.content, null, FIRST_PAGE + 1L)
                },
                Response.ErrorListener { error ->
                }
            )
        )
    }

    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Long, Line>) {
        requestQueue.addToRequestQueue(
            GsonRequest<Page<Line>>(
                "${Fragmentator4000.apiUrl}/searchPhrase?phrase=$phrase&page=${params.key}&size=$PAGE_SIZE",
                Fragmentator4000.pageOfLinesType,
                mutableMapOf(),
                Response.Listener { response ->
                    val key = if (response.last) null else params.key + 1
                    if (key != null) {
                        callback.onResult(response.content, key)
                    }
                },
                Response.ErrorListener { error ->
                }
            )
        )
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Long, Line>) {
        requestQueue.addToRequestQueue(
            GsonRequest<Page<Line>>(
                "${Fragmentator4000.apiUrl}/searchPhrase?phrase=$phrase&page=${params.key}&size=$PAGE_SIZE",
                Fragmentator4000.pageOfLinesType,
                mutableMapOf(),
                Response.Listener { response ->
                    val key = if (params.key > 1) params.key - 1 else null
                    if (key != null) {
                        callback.onResult(response.content, key)
                    }
                },
                Response.ErrorListener { error ->
                }
            )
        )
    }
}