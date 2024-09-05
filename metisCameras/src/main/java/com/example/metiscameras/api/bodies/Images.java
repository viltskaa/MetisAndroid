package com.example.metiscameras.api.bodies;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Images {
    @SerializedName("main_image")
    private String mainImage;

    @SerializedName("side_image")
    private String sideImage;
}
