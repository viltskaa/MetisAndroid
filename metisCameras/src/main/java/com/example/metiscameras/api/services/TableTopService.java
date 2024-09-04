package com.example.metiscameras.api.services;

import com.example.metiscameras.api.bodies.AddTableTopBodies;
import com.example.metiscameras.api.responses.OnlyMsgResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TableTopService {
    @POST("add_table_top")
    Call<OnlyMsgResponse> addTableTop(@Body AddTableTopBodies requestBody);

}
