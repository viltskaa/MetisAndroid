package com.example.metiscameras.api.services;

import com.example.metiscameras.api.bodies.AddTableTopBody;
import com.example.metiscameras.api.bodies.LoginBody;
import com.example.metiscameras.api.responses.OnlyMsgResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthorizationService {
    @POST("login")
    Call<OnlyMsgResponse> login(@Body LoginBody requestBody);
}
