package com.example.metiscameras.api.responses;

import androidx.annotation.NonNull;

import java.io.Serializable;


/**
 * пока является частью вырезанного контента
 * (возможно потребуется в будущем для админ панельки).
 * Serializable для передачи в другую активность
 */
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
