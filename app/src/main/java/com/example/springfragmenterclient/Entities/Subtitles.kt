package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose
import java.io.Serializable

open class Subtitles : Serializable {
    @Expose
    var id: Long = 0
    @Expose
    var filename: String = ""
    @Expose
    var filteredLines: MutableList<Line> = mutableListOf()
}
