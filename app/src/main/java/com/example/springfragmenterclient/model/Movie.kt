package com.example.springfragmenterclient.model

import com.google.gson.annotations.Expose

import java.io.Serializable

data class Movie(
    @Expose
    var id: Long?,
    @Expose
    var subtitles: Number,
    @Expose
    var fileName: String,
    @Expose
    var path: String,
    @Expose
    var parsedTitle: String?,
    @Expose
    var year: Int?,
    @Expose
    var resolution: String?
) : Serializable
