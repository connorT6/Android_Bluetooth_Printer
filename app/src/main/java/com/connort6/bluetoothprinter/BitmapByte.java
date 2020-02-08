package com.connort6.bluetoothprinter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BitmapByte {
    private static int height;
    private static int width;
    public static OutputStream outputStream;
    public static void setOutputStream(OutputStream outputStream){
        BitmapByte.outputStream = outputStream;
    }
    private static byte[] byteFromBitmap(Bitmap bitmap) {
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        ArrayList<String> bitImage = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            StringBuffer sb = new StringBuffer();
            for (int j = 0; j < width; j++) {
                int color = bitmap.getPixel(j, i);
                int x = color >> 16;
                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    sb.append("0");
                else
                    sb.append("1");
            }
            bitImage.add(sb.toString());
        }
        ArrayList<Integer> intImage = new ArrayList<>();
        /*intImage.add(29);
        intImage.add(118);
        intImage.add(48);
        intImage.add(0);
        intImage.add(72);
        intImage.add(0);
        intImage.add(200);
        intImage.add(0);

         */


        for (String string : bitImage) {
            intImage.add(29);
            intImage.add(118);
            intImage.add(48);
            intImage.add(0);
            intImage.add(72);
            intImage.add(0);
            intImage.add(1);
            intImage.add(0);
            int x = width;
            for (int i = 0; i < string.length(); i+=8) {
                String st = string.substring(i,i+8);
                int val = Integer.parseInt(st,2);
                intImage.add(val);
            }
            //int hex = Integer.parseInt(string, 2);
            //intImage.add(hex);
        }
        ArrayList<Byte> byteImage = new ArrayList<>();
        for (Integer integer : intImage) {
            byteImage.add(integer.byteValue());
        }

        byte[] array = new byte[byteImage.size()];

        for (int i = 0; i < byteImage.size(); i++) {
            array[i] = byteImage.get(i);
        }
        return array;
    }

    public static void createImageFromString(final String text) throws IOException {
        String dash = "";
        for (int i = 0; i < 33; i++) {
            dash += "_ ";
        }

        dash += "_";
        String fontPath = "res/font/aaa.ttf"; /* You can use any font or       use default */
        //Typeface tf = Typeface.createFromAsset(this, String.valueOf(R.font.aaa));
        // Typeface tf = ResourcesCompat.getFont(MainActivity.getMainContext(), R.font.aaa);
        int height = 0;
        String[] rows = text.split("\n");
        height = 31 * (rows.length + 1) + 5;/* Specify Length of Image File */
        FileOutputStream fop = null;
        File file;
        /* Specify the path where you want to save the image */
        file = new File("/storage/emulated/0/print.png");
        final Paint textPaint = new Paint() {
            {
                setColor(Color.WHITE);
                setTextAlign(Align.LEFT);
                setTextSize(25f);
                setAntiAlias(true);

            }
        };
        /* Optional to set Rect */
        final Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        //lblPrinterName.setText(String.valueOf(textPaint.measureText(rows[0])));
        final Bitmap bmp = Bitmap.createBitmap(576, height, Bitmap.Config.ARGB_8888);//use ARGB_8888 for better quality

        final Canvas canvas = new Canvas(bmp);
        textPaint.setStyle(Paint.Style.FILL); //fill the background with blue color
        canvas.drawRect(0, 0, 576, height, textPaint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTypeface(null);
        float y = 28;
        /* Custom your layout here */
        for (int i = -1; i < rows.length; i++) {
            if (i == -1) {
                canvas.drawText(dash, 5, y, textPaint);
            } else {
                if (i == (rows.length - 8)) {
                    textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    textPaint.setTextSize(28f);
                } else {
                    textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    textPaint.setTextSize(25f);
                }
                canvas.drawText(rows[i], 0, y, textPaint);
            }
            y = y + 28;
        }
        Paint paint = new Paint();
        FileOutputStream stream = new FileOutputStream(file); //create your FileOutputStream here
        print(bmp);
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        bmp.recycle();
        stream.close();
    }

    private static void print(Bitmap bitmap) {
        byte[] printFormat = new byte[]{0x1D, 0x72, 0x01};
        try {
            outputStream.write(printFormat);
            if (bitmap != null) {
                byte[] cmd = byteFromBitmap(bitmap);

                outputStream.write(cmd);
            } else {
                Log.e("Print Photo error", "the file doesn't exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
