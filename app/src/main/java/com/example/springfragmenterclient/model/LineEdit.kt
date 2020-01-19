package com.example.springfragmenterclient.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class LineEdit(
    val id: Long?,

    var fragmentRequestId: Long?,

    @Expose
    val lineId: Long,

    @Expose
    var text: String
) : Serializable