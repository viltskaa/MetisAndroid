package com.example.metiscameras.api;

import static com.example.metiscameras.api.URLS.TABLE_TOP;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.metiscameras.api.bodies.AddTableTopBodies;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.responses.OnlyMsgResponse;
import com.example.metiscameras.api.services.TableTopService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TableTopApi {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";

    private static final Retrofit retrofit = ApiClient.getClient(TABLE_TOP.getUrl());
    private static final TableTopService tableTopService = retrofit.create(TableTopService.class);

    public static void addTableTop(FindPatternResponse pattern) {
        if (DEBUG) Log.d(TAG, "TableTopApi -- addTableTop");

        Call<OnlyMsgResponse> call = tableTopService.addTableTop(new AddTableTopBodies(pattern));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OnlyMsgResponse> call, @NonNull Response<OnlyMsgResponse> response) {
                Log.i(TAG, response.toString());
                if (response.body() == null) {
                    if (DEBUG)
                        Log.i(TAG, "TableTopPatternApi - findPattern - response.body() == null");
                    return;
                }

                if (DEBUG) Log.i(TAG, response.body().toString());

                OnlyMsgResponse resp = Objects.requireNonNull(response.body());
                if (DEBUG) Log.i(TAG, "Response code " + response.code() + "/ Message: " + resp.getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<OnlyMsgResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
