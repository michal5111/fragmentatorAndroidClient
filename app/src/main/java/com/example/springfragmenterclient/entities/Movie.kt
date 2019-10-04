package com.example.springfragmenterclient.entities

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
    var path: String
) : Serializable
