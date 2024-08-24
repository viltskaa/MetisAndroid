package com.example.metisscan.api;


import com.example.metisscan.model.ScanResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("receive_string")
    Call<Void> sendScannedResult(@Body ScanResult scanResult);

    @GET("generate_qr")
    Call<ResponseBody> fetchQrCode();

    @POST("processing_cv")
    Call<ResponseBody> processImage(@Body RequestBody requestBody);

    @POST("add_pattern")
    Call<ResponseBody> addPattern(@Body RequestBody requestBody);
}