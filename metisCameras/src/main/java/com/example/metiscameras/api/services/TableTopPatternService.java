package com.example.metiscameras.api.services;

import com.example.metiscameras.api.bodies.Images;
import com.example.metiscameras.api.responses.AddPatternResponse;
import com.example.metiscameras.api.responses.FindPatternResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TableTopPatternService {

    @POST("add_pattern")
    Call<AddPatternResponse> addPattern(@Body Images requestBody);

    @POST("find_pattern")
    Call<FindPatternResponse> findPattern(@Body Images requestBody);
}
