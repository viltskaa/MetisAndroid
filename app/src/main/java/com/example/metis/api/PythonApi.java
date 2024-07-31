package com.example.metis.api;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.metis.model.ScanResult;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PythonApi {

    private static final String BASE_URL = "http://10.0.2.2:5000/v1/android/";
    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
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
}
