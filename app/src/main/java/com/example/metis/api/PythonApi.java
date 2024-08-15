package com.example.metis.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.metis.model.ScanResult;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PythonApi {

    private static final String BASE_URL = "http://10.0.2.2:5000/v1/android/";
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Время ожидания соединения
            .writeTimeout(60, TimeUnit.SECONDS)    // Время ожидания записи
            .readTimeout(60, TimeUnit.SECONDS)     // Время ожидания чтения
            .build();
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final ApiService apiService = retrofit.create(ApiService.class);

    public static void sendScannedResultToServer(String scannedString, Runnable callback) {
        ScanResult scanResult = new ScanResult(scannedString);
        Call<Void> call = apiService.sendScannedResult(scanResult);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ServerResponse", "String sent successfully");
                    if (callback != null) {
                        callback.run();
                    }
                } else {
                    Log.e("ServerError", "Failed to send scanned string. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void fetchQrCodeFromServer(Activity activity, ImageView imageView) {
        Call<ResponseBody> call = apiService.fetchQrCode();

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        activity.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServerError", "Failed to fetch QR code. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void processImage(Activity activity, Bitmap bitmap, ImageView imageView) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );

        Call<ResponseBody> call = apiService.processImage(requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("ServerResponse", "Response: " + responseBody);

                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String imgBase64 = jsonResponse.getString("imgBase64");
                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        Bitmap processedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        activity.runOnUiThread(() -> imageView.setImageBitmap(processedBitmap));

                        Log.d("Contours", jsonResponse.getJSONArray("contours").toString());
                        Log.d("Colors", jsonResponse.getJSONArray("colors").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServerError", "Failed to process image. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public static void addPattern(Activity activity, Bitmap bitmap, ImageView imageView) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("image", imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonObject.toString()
        );

        Call<ResponseBody> call = apiService.addPattern(requestBody);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("ServerResponse", "Response: " + responseBody);

                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String imgBase64 = jsonResponse.getString("imgBase64");
                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        Bitmap processedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        activity.runOnUiThread(() -> imageView.setImageBitmap(processedBitmap));

                        Log.d("Contours", jsonResponse.getJSONArray("contours").toString());
                        Log.d("Colors", jsonResponse.getJSONArray("colors").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("ServerError", "Failed to add pattern. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
