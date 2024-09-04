package com.example.metiscameras.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindPatternResponse {
    @SerializedName("msg")
    private String message;

    @SerializedName("pattern_id")
    private Integer id;

    private String article;

    private String name;

    private String material;

    @SerializedName("pattern_width")
    private Float width;

    @SerializedName("pattern_height")
    private Float height;

    @SerializedName("pattern_perimeter")
    private Float perimeter;

    @SerializedName("pattern_depth")
    private Float depth;

    @SerializedName("pattern_image_base64")
    private String image;

    @SerializedName("perimeter")
    private Float tableTopPerimeter;

    @SerializedName("width")
    private Float tableTopWidth;

    @SerializedName("height")
    private Float tableTopHeight;

    private List<List<Integer>> colors;

    @SerializedName("image_base64")
    private String tableTopImage;
}
