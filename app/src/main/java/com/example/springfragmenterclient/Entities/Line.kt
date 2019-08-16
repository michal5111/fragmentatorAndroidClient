package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose

import java.io.Serializable


data class Line(
    @Expose
    var id: Long,
    @Expose
    var number: Int,
    @Expose
    var timeString: String,
    @Expose
    var textLines: String,
    var parent: Movie? = null
) : Serializable
