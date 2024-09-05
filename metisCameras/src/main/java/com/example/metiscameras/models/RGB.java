package com.example.metiscameras.models;

import android.graphics.Color;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RGB {
    private int red;
    private int green;
    private int blue;
    private int color;

    public RGB(String hex) {
        color = Color.parseColor(hex);
        red = Color.red(color);
        green = Color.green(color);
        blue = Color.blue(color);
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + red + " " +  green + " " + blue + "]";
    }
}
