package com.example.metiscameras.api.responses;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//@Getter
//@Setter
//@AllArgsConstructor
public class OnlyMsgResponse {
    @SerializedName("msg")
    private String message;

    public OnlyMsgResponse(String msg){
        message = msg;
    }

    public String getMessage() {
        return message;
    }
}
