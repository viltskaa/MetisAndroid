package com.example.metiscameras.models.responses;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
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
