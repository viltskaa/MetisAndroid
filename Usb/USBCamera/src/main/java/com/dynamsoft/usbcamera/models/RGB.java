package com.dynamsoft.usbcamera.models;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

public class RGB {
    private int red;
    private int green;
    private int blue;

    public RGB(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public RGB(JSONArray colors) {
        try {
            this.red = colors.getInt(0);
            this.green = colors.getInt(1);
            this.blue = colors.getInt(2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "[" + red + " " +  green + " " + blue + "]";
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }
}
