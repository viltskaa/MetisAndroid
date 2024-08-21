package com.dynamsoft.usbcamera;

import com.dynamsoft.usbcamera.models.Image;
import com.dynamsoft.usbcamera.models.Images;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("receive_image")
    Call<Void> sendImage(@Body Image image);

    @POST("processing_cv")
    Call<ResponseBody> processImage(@Body Image requestBody);

    @POST("process_images")
    Call<ResponseBody> processImages(@Body Images requestBody);

}
