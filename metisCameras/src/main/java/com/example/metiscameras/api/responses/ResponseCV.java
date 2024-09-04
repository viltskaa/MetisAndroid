package com.example.metiscameras.api.responses;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseCV {

    private String imgBase64;

    private JSONArray contours;

    private JSONArray colors;


    @NonNull
    @Override
    public String toString() {
        return imgBase64 + " | " + contours.toString() + " | " + colors.toString();
    }
}
