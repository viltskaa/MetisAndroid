package com.example.metiscameras.api;

import static com.example.metiscameras.api.URLS.AUTH;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.metiscameras.activities.AuthorizationActivity;
import com.example.metiscameras.api.bodies.AddTableTopBody;
import com.example.metiscameras.api.bodies.LoginBody;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.responses.OnlyMsgResponse;
import com.example.metiscameras.api.services.AuthorizationService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AuthorizationApi {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";

    private static final Retrofit retrofit = ApiClient.getClient(AUTH.getUrl());
    private static final AuthorizationService authService = retrofit.create(AuthorizationService.class);

    public static void login(AuthorizationActivity activity, String login, String password) {
        if (DEBUG) Log.d(TAG, "AuthorizationApi -- login");

        Call<OnlyMsgResponse> call = authService.login(new LoginBody(login, password));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<OnlyMsgResponse> call, @NonNull Response<OnlyMsgResponse> response) {
                Log.i(TAG, response.toString());
                if (response.body() == null) {
                    if (DEBUG)
                        Log.i(TAG, "AuthorizationApi -- login -- response.body() == null");
                    return;
                }

                if (DEBUG) Log.i(TAG, response.body().toString());

                OnlyMsgResponse resp = Objects.requireNonNull(response.body());
                if (DEBUG)
                    Log.i(TAG, "AuthorizationApi -- login -- Response code " + response.code() + "/ Message: " + resp.getMessage());

                if(response.code() == 200)
                    activity.onLogin(true, resp.getMessage());
                else
                    activity.onLogin(false, resp.getMessage());
            }

            @Override
            public void onFailure(@NonNull Call<OnlyMsgResponse> call, @NonNull Throwable t) {
                Log.w(TAG, "AuthorizationApi -- login -- onFailure: " + t.getMessage());
                activity.onLogin(false, "Неизвестная ошибка.");
                t.printStackTrace();
            }
        });
    }
}
