package com.example.metiscameras.models.responses;

import androidx.annotation.NonNull;

import org.json.JSONArray;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FindPatternResponse{
//    private String result;
//    private int id;
//    private String aritcle;
//    private String name;
//    private String material;
//    private float width;
//    private float height;
//    private float perimeter;
//    private Float depth;
//    private String image;
//    private float tableTopPerimeter;
//    private float tableTopWidth;
//    private float tableTopHeight;
    private List<JSONArray> tableTopColors;
//    private List<List<Integer>> tableTopColors;
//    private String tableTopImage;


    @NonNull
    @Override
    public String toString() {
        return tableTopColors.toString();
    }
}
/*
@android.route('/test', methods=["POST"], endpoint='test')
def test():
        return current_app.response_class(
        response=json.dumps({
    'tableTopColors': [[1, 2, 3], [4, 5, 6]]
}),
status=200,
mimetype='application/json'
        )


 */