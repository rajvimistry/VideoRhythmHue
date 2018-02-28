package com.example.priyankadesai.videorhythmhue;

/**
 * Created by rajvi on 04-12-2017.
 */

import android.graphics.Bitmap;
import android.graphics.Color;

class AverageColor {

    private int mR;
    private int mG;
    private int mB;

    private AverageColor(int r, int g, int b) {
        mR = r;
        mG = g;
        mB = b;
    }

    static AverageColor fromBitmap(Bitmap bitmap, int pixelSpacing) {
        if (bitmap == null) {
            return new AverageColor(0, 0, 0);
        }
        int R = 0;
        int G = 0;
        int B = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int n = 0;
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixels.length; i += pixelSpacing) {
            int color = pixels[i];
            R += Color.red(color);
            G += Color.green(color);
            B += Color.blue(color);
            n++;
        }
        return new AverageColor(R / n, G / n, B / n);
    }

    public int getR() {
        return mR;
    }

    public int getG() {
        return mG;
    }

    public int getB() {
        return mB;
    }

    @Override
    public String toString() {
        return "AverageColor{" +
                "mR=" + mR +
                ", mG=" + mG +
                ", mB=" + mB +
                '}';
    }
}
