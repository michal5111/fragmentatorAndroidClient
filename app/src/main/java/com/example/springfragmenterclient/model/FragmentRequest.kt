package com.example.springfragmenterclient.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class FragmentRequest(
    @Expose
    var id: Long? = null,
    @Expose
    var movieId: Long? = null,
    @Expose
    var startOffset: Double = 0.0,
    @Expose
    var stopOffset: Double = 0.0,
    @Expose
    var startLineId: Long? = null,
    @Expose
    var stopLineId: Long? = null,

    var status: String = "PENDING",
    var errorMessage: String? = null,
    @Expose
    val lineEdits: MutableList<LineEdit> = emptyList<LineEdit>().toMutableList()
) : Serializable