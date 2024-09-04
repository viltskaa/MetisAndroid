package com.example.metiscameras;


import com.example.metiscameras.models.bodies.Image;
import com.example.metiscameras.models.bodies.Images;
import com.example.metiscameras.models.responses.FindPatternResponse;
import com.example.metiscameras.models.responses.PatternResponse;
import com.example.metiscameras.models.responses.UpdatePatternBody;

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

    @POST("update")
    Call<ResponseBody> updatePattern(@Body UpdatePatternBody requestBody);

    @POST("find_pattern")
    Call<FindPatternResponse> findPattern(@Body Images requestBody);

    @POST("test")
    Call<FindPatternResponse> test(@Body Image requestBody);

}
