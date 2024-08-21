package com.dynamsoft.usbcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.dynamsoft.usbcamera.models.Image;
import com.dynamsoft.usbcamera.models.Images;
import com.dynamsoft.usbcamera.models.RGB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
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
    private static final String BASE_URL = "http://192.168.1.57:5000/v1/android/";

    // Настраиваем OkHttpClient с тайм-аутами
    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS) // Время ожидания соединения
            .writeTimeout(120, TimeUnit.SECONDS)   // Время ожидания записи
            .readTimeout(120, TimeUnit.SECONDS)    // Время ожидания чтения
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final ApiService apiService = retrofit.create(ApiService.class);

    private static final String TAG = CaptureActivity.TAG;

    public static void sendImageToServer(String scannedString) {
        Image image = new Image(scannedString);
        Call<Void> call = apiService.sendImage(image);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "String sent successfully");

                } else {
                    Log.e(TAG, "Failed to send image. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public static void processImage(Activity activity, Bitmap bitmap, ImageView imageView, Runnable callback) {
        Log.d(TAG, "processImage");

        Call<ResponseBody> call = apiService.processImage(new Image(toBase64String(bitmap)));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
//                        Log.d(TAG, "Response: " + responseBody);
                        Log.d(TAG, "Response isSuccessful");

                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String imgBase64 = jsonResponse.getString("imgBase64");
                        byte[] decodedString = Base64.decode(imgBase64, Base64.DEFAULT);
                        Bitmap processedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                        activity.runOnUiThread(() -> {
                            imageView.setImageBitmap(processedBitmap);
                            ListView colors = (ListView) activity.findViewById(R.id.colors);

                            List<RGB> rgb = new ArrayList<RGB>();

                            try {
                                JSONArray respColors = jsonResponse.getJSONArray("colors");
                                for(int i = 0; i < respColors.length(); i++){
                                    rgb.add(new RGB(respColors.getJSONArray(i)));
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            ColorsAdapter adapter = new ColorsAdapter(activity, R.id.colors, rgb);
                            colors.setAdapter(adapter);
                        });




                        Log.d(TAG, jsonResponse.getJSONArray("contours").toString());
                        Log.d(TAG, String.valueOf(jsonResponse.getJSONArray("colors")));

                        if(callback != null)
                            callback.run();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Failed to process image. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
//                t.printStackTrace();
            }
        });
    }

    public static void processImages(Activity activity, Bitmap mainBitmap, Bitmap sideBitmap, ImageView imageView, Runnable callback) {
        Log.d(TAG, "processImageS");

        Call<ResponseBody> call = apiService.processImages(new Images(toBase64String(mainBitmap), toBase64String(sideBitmap)));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
//                        Log.d(TAG, "Response: " + responseBody);
                        Log.d(TAG, "Response isSuccessful");

                        JSONObject jsonResponse = new JSONObject(responseBody);

                        Bitmap processedBitmap = toBitmap(jsonResponse.getString("imgBase64"));

                        activity.runOnUiThread(() -> {
                            imageView.setImageBitmap(processedBitmap);
                            ListView colors = (ListView) activity.findViewById(R.id.colors);

                            List<RGB> rgb = new ArrayList<>();

                            try {
                                JSONArray respColors = jsonResponse.getJSONArray("colors");
                                for(int i = 0; i < respColors.length(); i++){
                                    rgb.add(new RGB(respColors.getJSONArray(i)));
                                }

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            ColorsAdapter adapter = new ColorsAdapter(activity, R.id.colors, rgb);
                            colors.setAdapter(adapter);
                        });

                        Log.d(TAG, jsonResponse.getJSONArray("contours").toString());
                        Log.d(TAG, String.valueOf(jsonResponse.getJSONArray("colors")));

                        if(callback != null)
                            callback.run();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e(TAG, "Failed to process image. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
//                t.printStackTrace();
            }
        });
    }

    private static String toBase64String(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private static Bitmap toBitmap(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
