package com.example.metiscameras.models;


import org.json.JSONArray;


public class FindPatternResponse{
    private String result;
    private int id;
    private String aritcle;
    private String name;
    private String material;
    private float width;
    private float height;
    private float perimeter;
    private Float depth;
    private String image;
    private float tableTopPerimeter;
    private float tableTopWidth;
    private float tableTopHeight;
    private List<JSONArray> tableTopColors;
    private String tableTopImage;

}

//response=json.dumps({'success': 'Table top successfully find',
//        'pattern_id': ttp_id,
//        'article': pattern.article,
//        'name': pattern.name,
//        'material': pattern.material,
//        'pattern_width': pattern.width,
//        'pattern_height': pattern.height,
//        'pattern_perimeter': pattern.perimeter,
//        'pattern_depth': pattern.depth,
//        'pattern_image_base64': path_to_base64(pattern.image_path),
//        'perimeter': perimeter,
//        'width': width,
//        'height': height,
//        'colors': colors,
//        'image_base64': main_image_base64