package com.example.metiscameras.api;

import static com.example.metiscameras.api.URLS.ANDROID;
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
import com.example.metiscameras.api.bodies.Images;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.services.AndroidService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AndroidApi {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";

    private static final Retrofit retrofit = ApiClient.getClient(ANDROID.toString());
    private static final AndroidService service = retrofit.create(AndroidService.class);

    public static void findPattern(MainActivity activity,
                                   Bitmap mainBitmap,
                                   Bitmap sideBitmap) {
        if (DEBUG) Log.d(TAG, "findPattern");

        Call<FindPatternResponse> call = service.findPattern(new Images(
                toBase64String(mainBitmap),
                toBase64String(sideBitmap)));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<FindPatternResponse> call, @NonNull Response<FindPatternResponse> response) {
                if (DEBUG) Log.d(TAG, "Response code " + response.code());

                FindPatternResponse resp = response.body();

                if (response.isSuccessful() && resp != null) {
                    try {
                        if (DEBUG) Log.i(TAG, "Message: " + resp.getMessage());
                        if (DEBUG) Log.d(TAG, response.body().toString());

                        if (DEBUG) Log.d(TAG, resp.getColors().toString());

                        activity.setPattern(resp);
                        activity.runOnUiThread(() -> {
                            if(resp.getArticle() != null)
                                ((TextView) activity.findViewById(R.id.article_view)).setText(resp.getArticle());

                            if(resp.getName() != null)
                                ((TextView) activity.findViewById(R.id.name_view)).setText(resp.getName());

                            if(resp.getMaterial() != null)
                                ((TextView) activity.findViewById(R.id.material_view)).setText(resp.getMaterial());
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (response.code() == 400 && resp != null) {
                    if (DEBUG) Log.i(TAG, "Message: " + resp.getMessage());
                    Toast.makeText(activity, "Паттерн не был найден", Toast.LENGTH_SHORT).show();
                    PythonApi.test(activity);
                }
                else if(resp != null) {
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

}
