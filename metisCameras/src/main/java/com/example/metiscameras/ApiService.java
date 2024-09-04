package com.example.metiscameras;


import com.example.metiscameras.api.bodies.Image;
import com.example.metiscameras.api.bodies.Images;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.api.responses.PatternResponse;

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


    @POST("find_pattern")
    Call<FindPatternResponse> findPattern(@Body Images requestBody);

    @POST("test")
    Call<FindPatternResponse> test(@Body Image requestBody);

}
