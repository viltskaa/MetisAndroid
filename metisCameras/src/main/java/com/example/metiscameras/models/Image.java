package com.example.metiscameras.models;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("image")
    String stringImage;

    public Image(String stringImage) {
        this.stringImage = stringImage;
    }
}