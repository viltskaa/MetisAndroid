package com.example.metiscameras.api.responses;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OnlyMsgResponse {
    @SerializedName("msg")
    private String message;
}
