package com.example.metiscameras.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PatternResponse {

    boolean isNew;

    int id;

    String article;

    String name;

    String material;

    public PatternResponse(
            boolean isNew,
            int id,
            String article,
            String name,
            String material)
    {
        this.isNew = isNew;
        this.id = id;
        this.article = article;
        this.name = name;
        this.material = material;
    }

    @NonNull
    @Override
    public String toString() {
        // FIXME затычка
        return "пока затычка";
    }
}
