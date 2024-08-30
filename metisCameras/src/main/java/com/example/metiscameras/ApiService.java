package com.example.metiscameras;


import com.example.metiscameras.models.Image;
import com.example.metiscameras.models.Images;
import com.example.metiscameras.models.PatternResponse;
import com.example.metiscameras.models.ResponseCV;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("receive_image")
    Call<Void> sendImage(@Body Image image);

    @POST("processing_cv")
    Call<ResponseBody> processImage(@Body Image requestBody);

    @POST("add_pattern")
    Call<PatternResponse> addPattern(@Body Image requestBody);

    @POST("process_images")
    Call<ResponseBody> processImages(@Body Images requestBody);
}
