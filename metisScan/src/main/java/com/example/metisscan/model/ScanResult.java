package com.example.metisscan.model;

import com.google.gson.annotations.SerializedName;

public class ScanResult {
    @SerializedName("scanned_string")
    String scannedString;

    public ScanResult(String scannedString) {
        this.scannedString = scannedString;
    }
}
