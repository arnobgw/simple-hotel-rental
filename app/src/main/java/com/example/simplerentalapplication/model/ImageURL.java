package com.example.simplerentalapplication.model;

import com.google.gson.annotations.SerializedName;

public class ImageURL {
    @SerializedName("url")
    public String url;

    public ImageURL(String url) {
        this.url = url;
    }
}
