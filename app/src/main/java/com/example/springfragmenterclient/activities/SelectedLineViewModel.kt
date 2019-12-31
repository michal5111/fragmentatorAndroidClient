package com.example.springfragmenterclient.activities

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.springfragmenterclient.adapters.LineEditViewAdapter
import com.example.springfragmenterclient.entities.FragmentRequest
import com.example.springfragmenterclient.entities.Line
import com.example.springfragmenterclient.entities.LineEdit
import com.example.springfragmenterclient.entities.Movie
import com.example.springfragmenterclient.repositories.LineRepository

class SelectedLineViewModel: ViewModel() {

    private val lineRepository = LineRepository()

    internal lateinit var selectedMovie: Movie
    internal lateinit var selectedLine: Line
    internal var fragmentRequest: FragmentRequest = FragmentRequest()

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
}