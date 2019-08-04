package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose

import java.io.Serializable

class Movie : Serializable {
    @Expose
    var id: Long = 0
    @Expose
    var subtitles: Subtitles = Subtitles()
    @Expose
    var fileName: String = ""
    @Expose
    var path: String = ""
    @Expose
    var startOffset: Double = 0.0
    @Expose
    var stopOffset: Double = 0.0

}
