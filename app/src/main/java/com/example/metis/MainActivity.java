package com.example.metis;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.metis.api.PythonApi;

import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 3;
    private static final int GALLERY_REQUEST = 4;
    private ImageView imageView;
    private TextView textView;
    private String scannedResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        this.imageView = this.findViewById(R.id.imageView1);
        this.textView = this.findViewById(R.id.textView);
        Button photoButton = this.findViewById(R.id.button);
        Button galleryButton = this.findViewById(R.id.galleryButton);

        photoButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        });

        galleryButton.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
        });

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            char pressedKey = (char) event.getUnicodeChar();
            if (pressedKey == '\n') {
                textView.setText(scannedResult);
                PythonApi.sendScannedResultToServer(scannedResult, () -> PythonApi.fetchQrCodeFromServer(this, imageView));
                scannedResult = "";
                return true;
            } else {
                scannedResult += pressedKey;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            Bitmap photo = null;
            if (requestCode == CAMERA_REQUEST) {
                photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            } else if (requestCode == GALLERY_REQUEST) {
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (photo != null) {
                imageView.setImageBitmap(photo);
                PythonApi.processImage(this, photo, imageView);
            }
        }
    }
}