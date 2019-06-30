package com.example.springfragmenterclient.Entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Movie implements Serializable {
    @Expose
    private SubtitlesFile subtitles;
    @Expose
    private String fileName;
    @Expose
    private String path;
    private String extension;

    public SubtitlesFile getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(SubtitlesFile subtitles) {
        this.subtitles = subtitles;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Movie() {
    }

    public Movie(SubtitlesFile subtitles, String fileName, String path, String extension) {
        this.subtitles = subtitles;
        this.fileName = fileName;
        this.path = path;
        this.extension = extension;
    }
}
