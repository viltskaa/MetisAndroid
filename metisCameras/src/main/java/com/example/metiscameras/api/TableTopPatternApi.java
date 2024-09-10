package com.example.metiscameras.api;

import static com.example.metiscameras.api.URLS.ANDROID;
import static com.example.metiscameras.api.URLS.TABLE_TOP_PATTERN;
import static com.example.metiscameras.api.Utils.toBase64String;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.metiscameras.activities.MainActivity;
import com.example.metiscameras.api.bodies.Images;
import com.example.metiscameras.api.responses.AddPatternResponse;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.services.TableTopPatternService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TableTopPatternApi {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";

    private static final Retrofit retrofit = ApiClient.getClient(ANDROID.getUrl()); // FIXME поставить правильный юрл, когда аня полностью сделает
    private static final TableTopPatternService patternService = retrofit.create(TableTopPatternService.class);

    public static void findPattern(MainActivity activity,
                                   Bitmap mainBitmap,
                                   Bitmap sideBitmap) {
        if (DEBUG) Log.d(TAG, "TableTopPatternApi -- findPattern");

        Call<FindPatternResponse> call = patternService.findPattern(new Images(
                toBase64String(mainBitmap),
                toBase64String(sideBitmap)));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FindPatternResponse> call, @NonNull Response<FindPatternResponse> response) {
                if (response.body() == null) {
                    if (DEBUG)
                        Log.i(TAG, "TableTopPatternApi - findPattern - response.body() == null");
                    return;
                }
                FindPatternResponse resp = response.body();
                if (DEBUG)
                    Log.i(TAG, "Response code " + response.code() + "/ Message: " + resp.getMessage());

                if (response.isSuccessful()) {
                    try {
                        if (DEBUG) Log.i(TAG, resp.toString());

                        activity.setPattern(resp);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (response.code() == 400) {
                    if (DEBUG)
                        Log.w(TAG, "Response code: " + response.code() + "/ Message: " + resp.getMessage());
                    Toast.makeText(activity, "Паттерн не был найден", Toast.LENGTH_SHORT).show();
                    TableTopPatternApi.findPattern(activity, mainBitmap, sideBitmap);
                } else {
                    if (DEBUG)
                        Log.w(TAG, "Response code: " + response.code() + "/ Message: " + resp.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindPatternResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

                t.printStackTrace();
            }
        });
    }

    public static void addPattern(Bitmap mainBitmap,
                                  Bitmap sideBitmap) {
        if (DEBUG) Log.d(TAG, "TableTopPatternApi -- addPattern");

        Call<AddPatternResponse> call = patternService.addPattern(new Images(
                toBase64String(mainBitmap),
                toBase64String(sideBitmap)));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AddPatternResponse> call, @NonNull Response<AddPatternResponse> response) {
                if (response.body() == null) {
                    if (DEBUG) Log.i(TAG, "TableTopPatternApi -- findPattern -- response.body() == null");
                    return;
                }
                AddPatternResponse resp = Objects.requireNonNull(response.body());
                if (DEBUG) Log.i(TAG, "Response code " + response.code() + "/ Message: " + resp.getMessage());

            }

            @Override
            public void onFailure(@NonNull Call<AddPatternResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage());

                t.printStackTrace();
            }
        });
    }
}
