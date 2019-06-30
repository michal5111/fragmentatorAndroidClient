package com.example.springfragmenterclient.Entities;

import com.google.gson.annotations.Expose;

import java.io.Serializable;


public class Line implements Serializable {
    @Expose
    private int number;
    @Expose
    private String timeString;
    @Expose
    private String textLines;
    @Expose
    private double startOffset;
    @Expose
    private double stopOffset;
    private Movie parent;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTextLines() {
        return textLines;
    }

    public void setTextLines(String textLines) {
        this.textLines = textLines;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public double getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(double startOffset) {
        this.startOffset = startOffset;
    }

    public double getStopOffset() {
        return stopOffset;
    }

    public void setStopOffset(double stopOffset) {
        this.stopOffset = stopOffset;
    }

    public Line() {
    }

    public Movie getParent() {
        return parent;
    }

    public void setParent(Movie parent) {
        this.parent = parent;
    }
}
