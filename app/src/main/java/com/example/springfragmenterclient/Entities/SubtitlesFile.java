package com.example.springfragmenterclient.Entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

//@JsonDeserialize(as = SRTSubtitlesFile.class)
public class SubtitlesFile implements Serializable {

    private String filename;
    @Expose
    protected List<Line> filteredLines = new LinkedList<>();

    public List<Line> getFilteredLines() {
        return filteredLines;
    }

    public void setFilteredLines(List<Line> filteredLines) {
        this.filteredLines = filteredLines;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public SubtitlesFile() {
    }
}
