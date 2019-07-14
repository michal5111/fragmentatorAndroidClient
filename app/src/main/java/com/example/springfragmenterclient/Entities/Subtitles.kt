package com.example.springfragmenterclient.Entities

import com.google.gson.annotations.Expose
import java.io.Serializable

//@JsonDeserialize(as = SRTSubtitlesFile.class)
open class Subtitles : Serializable {

    @Expose
    var filename: String = ""
    @Expose
    var filteredLines: MutableList<Line> = mutableListOf()
}
