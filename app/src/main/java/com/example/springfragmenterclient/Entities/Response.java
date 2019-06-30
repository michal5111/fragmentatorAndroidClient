package com.example.springfragmenterclient.Entities;

import java.io.Serializable;

public class Response implements Serializable {
    private String url;

    public Response() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
