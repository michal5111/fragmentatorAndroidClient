package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose

import java.io.Serializable

data class Movie(
    @Expose
    var id: Long,
    @Expose
    var subtitles: Subtitles,
    @Expose
    var fileName: String,
    @Expose
    var path: String,
    @Expose
    var startOffset: Double = 0.0,
    @Expose
    var stopOffset: Double = 0.0
) : Serializable
