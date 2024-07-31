package com.example.metis.api;

import com.example.metis.model.ScanResult;

import okhttp3.ResponseBody;
import retrofit2.Call;

public interface ApiService {
    @retrofit2.http.POST("receive_string")
    Call<Void> sendScannedResult(@retrofit2.http.Body ScanResult scanResult);

    @retrofit2.http.GET("generate_qr")
    Call<ResponseBody> fetchQrCode();
}