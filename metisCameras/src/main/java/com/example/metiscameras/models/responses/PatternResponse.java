package com.example.metiscameras.models.responses;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class PatternResponse implements Serializable {
    private boolean isNew;

    private int id;

    private String article;

    private String name;

    private String material;



    @NonNull
    @Override
    public String toString() {
        // FIXME затычка
        return "пока затычка" + " " + isNew + " " + id;
    }
}
