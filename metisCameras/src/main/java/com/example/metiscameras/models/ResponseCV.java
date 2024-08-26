package com.example.metiscameras.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;

public class ResponseCV {

    String imgBase64;

    JSONArray contours;

    JSONArray colors;


    public ResponseCV(String imgBase64, JSONArray contours, JSONArray colors) {
        this.imgBase64 = imgBase64;
        this.contours = contours;
        this.colors = colors;
    }

    @NonNull
    @Override
    public String toString() {
        return imgBase64 + " | " + contours.toString() + " | " + colors.toString();
    }
}
