package com.example.metiscameras.api.bodies;

import com.example.metiscameras.api.responses.FindPatternResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class AddTableTopBodies {
    @SerializedName("ttp_id")
    private Integer id;
    private Float perimeter;
    private Float width;
    private Float height;
    @SerializedName("image_base64")
    private String image;
    private List<String> colors;

    public AddTableTopBodies(FindPatternResponse pattern) {
        id = pattern.getId();
        perimeter = pattern.getPerimeter();
        width = pattern.getWidth();
        height = pattern.getHeight();
        image = pattern.getImage();
        colors = pattern.getColors();
    }
}
