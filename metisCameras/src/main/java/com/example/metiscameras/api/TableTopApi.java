package com.example.metiscameras.api;

import static com.example.metiscameras.api.URLS.ANDROID;
import static com.example.metiscameras.api.URLS.TABLE_TOP;
import static com.example.metiscameras.api.Utils.toBase64String;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.metiscameras.PythonApi;
import com.example.metiscameras.R;
import com.example.metiscameras.activities.MainActivity;
import com.example.metiscameras.api.bodies.AddTableTopBodies;
import com.example.metiscameras.api.bodies.Images;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.responses.OnlyMsgResponse;
import com.example.metiscameras.api.services.AndroidService;
import com.example.metiscameras.api.services.TableTopService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TableTopApi {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";

    private static final Retrofit retrofit = ApiClient.getClient(TABLE_TOP.toString());
    private static final TableTopService service = retrofit.create(TableTopService.class);

    public static void addTableTop(FindPatternResponse pattern) {
        if (DEBUG) Log.d(TAG, "addTableTop");

        Call<OnlyMsgResponse> call = service.addTableTop(new AddTableTopBodies(
                pattern.getId(),
                pattern.getPerimeter(),
                pattern.getWidth(),
                pattern.getHeight(),
                pattern.getImage(),
                pattern.getColors()
        ));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OnlyMsgResponse> call, @NonNull Response<OnlyMsgResponse> response) {
                if (DEBUG) Log.d(TAG, "Response code " + response.code());


                if (response.isSuccessful() && response.body() != null) {
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {

                } else {
                    Log.w(TAG, "Failed to process image. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<OnlyMsgResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

                t.printStackTrace();
            }
        });
    }
}
