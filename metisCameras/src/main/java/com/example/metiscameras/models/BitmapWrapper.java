package com.example.metiscameras.models;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.nio.ByteBuffer;

public class BitmapWrapper {

    //TODO add builder

    private Bitmap bitmap;

    public BitmapWrapper(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public BitmapWrapper(int width, int height) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    }

    public BitmapWrapper(int width, int height, Bitmap.Config config) {
        bitmap = Bitmap.createBitmap(width, height, config);
    }

    public BitmapWrapper(int width, int height, ByteBuffer frame) {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.copyPixelsFromBuffer(frame);
    }

    public BitmapWrapper(int width, int height, Bitmap.Config config, ByteBuffer frame) {
        bitmap = Bitmap.createBitmap(width, height, config);
        bitmap.copyPixelsFromBuffer(frame);
    }

    public void updateBitmap(ByteBuffer frame){
        bitmap.copyPixelsFromBuffer(frame);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    /**
     * Обрезка изображения с начальной точки (x, y) до заданной ширины (width) и высоты (height)
     *
     * @param x координата начальной точки
     * @param y координата начальной точки
     * @param width ширина
     * @param height высота
     */
    public void crop(int x, int y, int width, int height) {
        int maxWidth = bitmap.getWidth();
        int maxHeight = bitmap.getHeight();

        // проверка координат
        if (x + width > maxWidth)
            width = maxWidth - x;

        if (y + height > maxHeight)
            height = maxHeight - y;

        bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    /**
     * Обрезка изображения с начальной точки (x, y) до заданной ширины (width) и высоты (height).
     * Возвращает получившийся после обрезки Bitmap
     *
     * @param x координата начальной точки
     * @param y координата начальной точки
     * @param width ширина
     * @param height высота
     * @return возвращает обрезанный новый Bitmap (текущий не меняет)
     */
    public Bitmap cropAndResult(int x, int y, int width, int height) {
        int maxWidth = bitmap.getWidth();
        int maxHeight = bitmap.getHeight();

        if (width < 0)
            width = - width;
        if (height < 0)
            height = - height;

        if (x + width > maxWidth)
            width = maxWidth - x;

        if (y + height > maxHeight)
            height = maxHeight - y;

        return Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    // Заполнение изображения цветом с заданными значениями RGB
    public void fillWithColor(int red, int green, int blue) {
        // Преобразуем значения RGB в цвет
        int color = Color.rgb(red, green, blue);

        // Создаем новый Bitmap и заполняем его этим цветом
        Bitmap coloredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(coloredBitmap);
        canvas.drawColor(color);

        bitmap = coloredBitmap;
    }

    public void fillWithColor(int color) {
        // Преобразуем значения RGB в цвет
//        int color = Color.rgb(colors.getRed(), colors.getGreen(), colors.getBlue());

        // Создаем новый Bitmap и заполняем его этим цветом
        Bitmap coloredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        coloredBitmap.eraseColor(color);

        bitmap = coloredBitmap;
    }

    // Преобразование в черно-белый Bitmap
    public BitmapWrapper toGrayscale() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayBitmap);
        Paint paint = new Paint();

        float[] matrix = {
                0.299f, 0.587f, 0.114f, 0, 0,
                0.299f, 0.587f, 0.114f, 0, 0,
                0.299f, 0.587f, 0.114f, 0, 0,
                0, 0, 0, 1, 0
        };

        android.graphics.ColorMatrix colorMatrix = new android.graphics.ColorMatrix(matrix);
        paint.setColorFilter(new android.graphics.ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);

        this.bitmap = grayBitmap;
        return this;
    }
}