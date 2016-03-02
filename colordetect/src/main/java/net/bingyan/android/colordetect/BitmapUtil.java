package net.bingyan.android.colordetect;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Jinge on 2016/2/29.
 */
public class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public static Bitmap average(Bitmap bitmap) {
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

    public static Bitmap averageBlock(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;
//        bitmap.copyPixelsToBuffer();
        Log.d(TAG, "byte count : " + bitmap.getByteCount());
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        // rgba
        byte[] bitmapBytes = byteBuffer.array();

        int blockSize = 5;

        byte[] data = new byte[49152];

        for (int j = 0; j < height / blockSize; j++) {
            for (int i = 0; i < width / blockSize; i++) {
                int s = (j * width + i) * blockSize;
                int redSum = 0;
                int greenSum = 0;
                int blueSum = 0;
                for (int p = 0; p < blockSize; p++) {
                    for (int q = 0; q < blockSize; q++) {
                        redSum += bitmapBytes[4 * (s + p * blockSize + q)];
                        greenSum += bitmapBytes[4 * (s + p * blockSize + q) + 1];
                        blueSum += bitmapBytes[4 * (s + p * blockSize + q) + 2];
                    }
                }
                int redA = redSum / blockSize / blockSize;
                int greenA = greenSum / blockSize / blockSize;
                int blueA = blueSum / blockSize / blockSize;

                int redS = 0;
                int greenS = 0;
                int blueS = 0;
                for (int p = 0; p < blockSize; p++) {
                    for (int q = 0; q < blockSize; q++) {
                        redS += Math.pow(redA - bitmapBytes[4 * (s + p * blockSize + q)], 2);
                        greenS += Math.pow(greenA - bitmapBytes[4 * (s + p * blockSize + q) + 1], 2);
                        blueS += Math.pow(blueA - bitmapBytes[4 * (s + p * blockSize + q) + 2], 2);
                    }
                }

//                float[] hsv = new float[3];
//
//                Color.RGBToHSV(redA >= 0 ? redA : redA + 0xff,
//                        greenA >= 0 ? greenA : greenA + 0xff,
//                        blueA >= 0 ? blueA : blueA + 0xff, hsv);

                if (redS + greenS + blueS < 10000) {
                    data[4 * (j * width / blockSize + i)] = (byte) redA;
                    data[4 * (j * width / blockSize + i) + 1] = (byte) greenA;
                    data[4 * (j * width / blockSize + i) + 2] = (byte) blueA;
                    data[4 * (j * width / blockSize + i) + 3] = (byte) 0xff;
                } else {
                    data[4 * (j * width / blockSize + i)] = (byte) 0xff;
                    data[4 * (j * width / blockSize + i) + 1] = (byte) 0xff;
                    data[4 * (j * width / blockSize + i) + 2] = (byte) 0xff;
                    data[4 * (j * width / blockSize + i) + 3] = (byte) 0xff;
                }

            }
        }
        Bitmap result = Bitmap.createBitmap(width / blockSize, height / blockSize, Bitmap.Config.ARGB_8888);
        result.copyPixelsFromBuffer(ByteBuffer.wrap(data));
        Log.d(TAG, "create time");
        result = Bitmap.createScaledBitmap(bitmap, 128, 96, false);
        Log.d(TAG, "get create");
        return result;
    }

    public static Bitmap HSVDetect(Bitmap bitmap) {
        int width = bitmap.getWidth() / 10;
        int height = bitmap.getHeight() / 10;
        Bitmap small = Bitmap.createScaledBitmap(bitmap, width, height, false);

        Log.d(TAG, "byte count : " + bitmap.getByteCount() + "  " + small.getByteCount());
        ByteBuffer byteBuffer = ByteBuffer.allocate(small.getByteCount());
        small.copyPixelsToBuffer(byteBuffer);
        // rgba
        byte[] bitmapBytes = byteBuffer.array();
        Log.d(TAG, "get bytes");

        int[] pixels = new int[width * height];
        small.getPixels(pixels, 0, width, 0, 0, width, height);
        Log.d(TAG, "get int");

        int index = 0;
        float[] HSV = new float[3];

        int left = width;
        int top = height;
        int right = 0;
        int bottom = 0;

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // convert to HSV
                Color.colorToHSV(pixels[index], HSV);
                // increase Saturation level
                //HSV[0] = Hue
                if (HSV[0] < 295 || HSV[0] > 333) {
//                    pixels[index] = 0xffffffff;
                } else {
                    if (x < left) left = x;
                    if (x > right) right = x;
                    if (y < top) top = y;
                    if (y > bottom) bottom = y;
                }
                pixels[index] = 0x00000000;
            }
        }
        for (int y = top; y <= bottom; y++) {
            pixels[y * width + left] = 0xff000000;
            pixels[y * width + right] = 0xff000000;
        }
        for (int x = left; x <= right; x++) {
            pixels[top * width + x] = 0xff000000;
            pixels[bottom * width + x] = 0xff000000;
        }
        Log.d(TAG, "hsv get");
        small.setPixels(pixels, 0, width, 0, 0, width, height);
        Bitmap result = Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.ARGB_8888);
        return result;
    }

    public static Bitmap openCVDetect(Bitmap bitmap) {
        Mat rgb = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8SC1);
        Utils.bitmapToMat(bitmap, rgb);
        Mat hsv = new Mat(bitmap.getWidth(), bitmap.getHeight(), CvType.CV_8SC1);
        Imgproc.cvtColor(rgb, hsv, Imgproc.COLOR_RGB2HSV);
        ColorBlobDetector detector = new ColorBlobDetector();
        Scalar colorHsv = converScalarRgba2Hsv(new Scalar(135, 67, 116, 255));
        Log.i(TAG, "hsv color: (" + colorHsv.val[0] + ", " + colorHsv.val[1] +
                ", " + colorHsv.val[2] + ", " + colorHsv.val[3] + ")");
        detector.setHsvColor(colorHsv);
        detector.process(rgb);

        List<MatOfPoint> contours = detector.getContours();
        Log.e(TAG, "Contours count: " + contours.size());
        Scalar CONTOUR_COLOR = new Scalar(135, 67, 116, 255);
        Imgproc.drawContours(rgb, contours, -1, CONTOUR_COLOR);
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / 4, bitmap.getHeight() / 4, Bitmap.Config.ARGB_8888);

        Mat tHsv = detector.getMask();

        Log.d(TAG, tHsv.rows() + "  " + tHsv.cols() + "   " + result.getWidth() + "  " + result.getHeight());
        Utils.matToBitmap(tHsv, result);

        return result;
    }

    private static Scalar converScalarRgba2Hsv(Scalar rgbColor) {
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgbColor);
        Mat pointMatHsv = new Mat();
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 4);
        return new Scalar(pointMatHsv.get(0, 0));
    }

    public static Bitmap ocDetectRGB(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Mat srcRgb = new Mat(width, height, CvType.CV_8SC1);
        Mat mHsvMat = new Mat();
        Mat mMask = new Mat();

        Scalar mLowerBound = new Scalar(0);
        Scalar mUpperBound = new Scalar(0);
        Scalar mColorRadius = new Scalar(50, 50, 50, 0);

        Scalar rgbColor = new Scalar(135, 67, 116, 255);
        Scalar hsvColor = converScalarRgba2Hsv(rgbColor);

        double minH = (hsvColor.val[0] >= mColorRadius.val[0]) ? hsvColor.val[0] - mColorRadius.val[0] : 0;
        double maxH = (hsvColor.val[0] + mColorRadius.val[0] <= 255) ? hsvColor.val[0] + mColorRadius.val[0] : 255;

        mLowerBound.val[0] = minH;
        mUpperBound.val[0] = maxH;
        mLowerBound.val[1] = hsvColor.val[1] - mColorRadius.val[1];
        mUpperBound.val[1] = hsvColor.val[1] + mColorRadius.val[1];
        mLowerBound.val[2] = hsvColor.val[2] - mColorRadius.val[2];
        mUpperBound.val[2] = hsvColor.val[2] + mColorRadius.val[2];
        mLowerBound.val[3] = 0;
        mUpperBound.val[3] = 255;

        Utils.bitmapToMat(bitmap, srcRgb);
        Imgproc.pyrDown(srcRgb, srcRgb);
        Imgproc.cvtColor(srcRgb, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);

        Bitmap result = Bitmap.createBitmap(mMask.cols(), mMask.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mMask, result);

        return result;
    }

}
