package com.example.metiscameras.api.bodies;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Image {
    @SerializedName("image")
    private String stringImage;
}
