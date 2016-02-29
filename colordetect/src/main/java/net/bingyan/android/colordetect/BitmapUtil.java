package net.bingyan.android.colordetect;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by Jinge on 2016/2/29.
 */
public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public static Bitmap decode(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;
//        bitmap.copyPixelsToBuffer();
        Log.d(TAG, "byte count : " + bitmap.getByteCount());
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        // rgba
        byte[] bitmapBytes = byteBuffer.array();

        Log.d(TAG, "first : " + bitmapBytes[0] + "  " + bitmapBytes[1] + "  " + bitmapBytes[2] + "  " + bitmapBytes[3]);
        Log.d(TAG, "second : " + bitmapBytes[4] + "  " + bitmapBytes[5] + "  " + bitmapBytes[6] + "  " + bitmapBytes[7]);
        Log.d(TAG, "bytes length : " + bitmapBytes.length);

        int redSum = 0;
        int greenSum = 0;
        int blueSum = 0;

        Log.d(TAG, "t1");
        for (int i = 0; i < pixelCount; i++) {
            redSum += bitmapBytes[4 * i];
            greenSum += bitmapBytes[4 * i + 1];
            blueSum += bitmapBytes[4 * i + 2];
        }
        Log.d(TAG, "t2");
        int redA = redSum / pixelCount;
        int greenA = greenSum / pixelCount;
        int blueA = blueSum / pixelCount;

        Log.d(TAG, "average : " + redA + "  " + greenA + "  " + blueA);
        Log.d(TAG, "average : " + (byte) redA + "  " + (byte) greenA + "  " + (byte) blueA);


        byte[] data = new byte[bitmapBytes.length];

        for (int i = 0; i < pixelCount; i++) {
            data[4 * i] = (byte) (bitmapBytes[4 * i] - (byte) redA);
            data[4 * i + 1] = (byte) (bitmapBytes[4 * i + 1] - (byte) greenA);
            data[4 * i + 2] = (byte) (bitmapBytes[4 * i + 2] - (byte) blueA);
            data[4 * i + 3] = (byte) 0xff;
        }
        Log.d(TAG, "t3");
        Bitmap result = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ByteBuffer.wrap(data));
        return result;
    }

}
