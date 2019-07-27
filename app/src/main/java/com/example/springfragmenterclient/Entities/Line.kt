package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose

import java.io.Serializable


class Line : Serializable {
    @Expose
    var number: Int = 0
    @Expose
    var timeString: String = ""
    @Expose
    var textLines: String = ""
    var parent: Movie? = null
}
