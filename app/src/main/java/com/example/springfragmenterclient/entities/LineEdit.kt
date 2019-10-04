package com.example.springfragmenterclient.entities

import com.google.gson.annotations.Expose

data class LineEdit(
    val id: Long,

    @Expose
    val fragmentRequestId: Long,

    @Expose
    val lineId: Long,

    @Expose
    var text: String
)