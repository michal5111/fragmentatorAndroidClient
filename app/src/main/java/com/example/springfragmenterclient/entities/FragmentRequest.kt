package com.example.springfragmenterclient.entities

import com.google.gson.annotations.Expose
import java.io.Serializable

data class FragmentRequest(
    @Expose
    var id: Long = -1L,
    @Expose
    var movieId: Long = -1L,
    @Expose
    var startOffset: Double = 0.0,
    @Expose
    var stopOffset: Double = 0.0,
    @Expose
    var startLineId: Long = -1L,
    @Expose
    var stopLineId: Long = -1L,
    @Expose
    var status: String = "PENDING",
    var errorMessage: String? = null,
    @Expose
    val lineEdits: MutableList<LineEdit> = emptyList<LineEdit>().toMutableList()
) : Serializable