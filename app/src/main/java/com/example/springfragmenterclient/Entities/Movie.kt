package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose

import java.io.Serializable

class Movie : Serializable {
    @Expose
    var subtitles: Subtitles = Subtitles()
    @Expose
    var fileName: String = ""
    @Expose
    var path: String = ""

}
