package com.example.springfragmenterclient.activities

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.adapters.LineEditViewAdapter
import com.example.springfragmenterclient.model.FragmentRequest
import com.example.springfragmenterclient.model.Line
import com.example.springfragmenterclient.model.LineEdit
import com.example.springfragmenterclient.model.Movie
import com.example.springfragmenterclient.repositories.LineRepository
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class SelectedLineViewModel
@Inject constructor(private val lineRepository: LineRepository) : ViewModel() {

    internal lateinit var selectedMovie: Movie
    internal lateinit var selectedLine: Line
    internal var fragmentRequest: FragmentRequest = FragmentRequest()
    val compositeDisposable = CompositeDisposable()

    fun getLineSnapshot(id: Long) =
        lineRepository.getLineSnapshot(id)

    internal fun setLineEdits(lineEditRecyclerView: RecyclerView) {
        fragmentRequest.lineEdits.clear()
        for (i in 0 until lineEditRecyclerView.childCount) {
            val holder: LineEditViewAdapter.ViewHolder = lineEditRecyclerView
                .getChildViewHolder(lineEditRecyclerView.getChildAt(i)) as LineEditViewAdapter.ViewHolder
            if (holder.edited) {
                fragmentRequest.lineEdits.add(
                    LineEdit(
                        null,
                        null,
                        selectedLine.id!!,
                        holder.lineTextEdit.text.toString()
                    )
                )
            }
        }
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}