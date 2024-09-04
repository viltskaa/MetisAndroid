package com.example.metiscameras.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePatternBody {
    private int id;

    private String article;

    private String name;

    private String material;
}
