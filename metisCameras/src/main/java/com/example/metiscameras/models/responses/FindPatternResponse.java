package com.example.metiscameras.models.responses;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FindPatternResponse {
    @SerializedName("msg")
    private String message;

    @SerializedName("pattern_id")
    private int id;

    private String article;

    private String name;

    private String material;

    @SerializedName("pattern_width")
    private float width;

    @SerializedName("pattern_height")
    private float height;

    @SerializedName("pattern_perimeter")
    private float perimeter;

    @SerializedName("pattern_depth")
    private Float depth;

    @SerializedName("pattern_image_base64")
    private String image;

    @SerializedName("perimeter")
    private float tableTopPerimeter;

    @SerializedName("width")
    private float tableTopWidth;

    @SerializedName("height")
    private float tableTopHeight;

    private List<List<Integer>> colors;

    @SerializedName("image_base64")
    private String tableTopImage;

    public FindPatternResponse(String message,
                               int id,
                               String article,
                               String name,
                               String material,
                               float width,
                               float height,
                               float perimeter,
                               Float depth,
                               String image,
                               float tableTopPerimeter,
                               float tableTopWidth,
                               float tableTopHeight,
                               List<List<Integer>> colors,
                               String tableTopImage) {
        this.message = message;
        this.id = id;
        this.article = article;
        this.name = name;
        this.material = material;
        this.width = width;
        this.height = height;
        this.perimeter = perimeter;
        this.depth = depth;
        this.image = image;
        this.tableTopPerimeter = tableTopPerimeter;
        this.tableTopWidth = tableTopWidth;
        this.tableTopHeight = tableTopHeight;
        this.colors = colors;
        this.tableTopImage = tableTopImage;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public String getArticle() {
        return article;
    }

    public String getName() {
        return name;
    }

    public String getMaterial() {
        return material;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getPerimeter() {
        return perimeter;
    }

    public Float getDepth() {
        return depth;
    }

    public String getImage() {
        return image;
    }

    public float getTableTopPerimeter() {
        return tableTopPerimeter;
    }

    public float getTableTopWidth() {
        return tableTopWidth;
    }

    public float getTableTopHeight() {
        return tableTopHeight;
    }

    public List<List<Integer>> getColors() {
        return colors;
    }

    public String getTableTopImage() {
        return tableTopImage;
    }
}
