package com.example.metiscameras.models;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.metiscameras.R;

import java.util.List;

public class ColorsAdapter extends ArrayAdapter<RGB> {

    private Context context;
    private List<RGB> colors;

    public ColorsAdapter(@NonNull Context context, int resource, @NonNull List<RGB> objects) {
        super(context, resource, objects);

        this.context = context;
        colors = objects;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_colors_item, parent, false);

        TextView rgb = (TextView) view.findViewById(R.id.put_rgb);
        rgb.setText(colors.get(position).toString());

        ImageView image = view.findViewById(R.id.image_color);

        BitmapWrapper bitmapW = new BitmapWrapper(1000, 1000);
        bitmapW.fillWithColor(colors.get(position).getColor());
        image.post(() -> {
            synchronized (bitmapW) {
                image.setImageBitmap(bitmapW.getBitmap());
            }
        });

        return view;
    }
}
