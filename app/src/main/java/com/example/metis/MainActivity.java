package com.example.metis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 3;
    private ImageView imageView;
    private TextView textView;
    private String scannedResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        this.imageView = this.findViewById(R.id.imageView1);
        this.textView = this.findViewById(R.id.textView);
        Button photoButton = this.findViewById(R.id.button);

        photoButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey == '\n') {
                textView.setText(scannedResult);
                sendScannedResultToServer(scannedResult);
                scannedResult = "";
                return true;
            } else {
                scannedResult += pressedKey;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            imageView.setImageBitmap(photo);
        }
    }

    private void sendScannedResultToServer(String scannedString) {
        new Thread(() -> {
            try {
                HttpURLConnection conn = getHttpURLConnection(scannedString);

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d("ServerResponse", response.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static @NonNull HttpURLConnection getHttpURLConnection(String scannedString) throws IOException {
        URL url = new URL("http://127.0.0.1:5000/v1/android/receive_string");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = "{\"scanned_string\": \"" + scannedString + "\"}";

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return conn;
    }

}