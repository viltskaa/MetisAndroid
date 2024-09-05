package com.example.metiscameras.api.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AddPatternResponse {
    @SerializedName("msg")
    private String message;

    @SerializedName("imgBase64")
    private String image;
    private Float perimeter;
    private Float width;
    private Float height;
    private List<String> colors;
}
