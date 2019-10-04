package com.example.springfragmenterclient.entities

import com.google.gson.annotations.Expose
import java.io.Serializable

data class Subtitles(
    @Expose
    var id: Long,
    @Expose
    var filename: String,
    @Expose
    var filteredLines: MutableList<Line> = mutableListOf()
) : Serializable
