package com.example.metiscameras.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum URLS {
    TABLE_TOP ("http://192.168.128.208:5000/v1/table_top/"),
    TABLE_TOP_PATTERN ("http://192.168.128.208:5000/v1/table_top_pattern/"),
    ANDROID ("http://192.168.128.208:5000/v1/android/");

    private final String url;
}
