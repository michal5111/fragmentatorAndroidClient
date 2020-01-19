package com.example.springfragmenterclient.model

import com.google.gson.annotations.Expose
import java.io.Serializable

data class Subtitles(
    @Expose
    var id: Long?,
    @Expose
    var filename: String,
    @Expose
    val movie: Movie
) : Serializable
