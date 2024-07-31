package com.example.metis.api;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PythonApi {

    public static void sendScannedResultToServer(String scannedString) {
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
        URL url = new URL("http://10.0.2.2:5000/v1/android/receive_string");
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
