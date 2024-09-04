package com.example.metiscameras.api.bodies;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Images {
    @SerializedName("maine")
    private String mainImage;

    @SerializedName("side")
    private String sideImage;
}
