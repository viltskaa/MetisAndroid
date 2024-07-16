package com.example.metis;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CameraUtils cum = new CameraUtils(getApplicationContext());
        try {
            System.out.println("!!!!!!!" + Arrays.toString(cum.getCameraIds()));
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}