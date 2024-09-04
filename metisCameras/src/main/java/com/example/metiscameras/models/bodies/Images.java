package com.example.metiscameras.models.bodies;

import com.google.gson.annotations.SerializedName;

public class Images {
    @SerializedName("maine")
    String mainImage;

    @SerializedName("side")
    String sideImage;

    public Images(String mainImage, String sideImage) {
        this.mainImage = mainImage;
        this.sideImage = sideImage;
    }
}
