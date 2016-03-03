package net.bingyan.android.wificar;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Created by Jinge on 2016/2/29.
 */
public class BitmapUtil {
    public static void decode(Bitmap bitmap) {
        int pixle = bitmap.getPixel(0, 0);
    }

    public static int getColorAtPt(Bitmap bitmap, float xCent, float yCent) {
        int x = (int) (bitmap.getWidth() * xCent);
        int y = (int) (bitmap.getHeight() * yCent);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Mat rgb = new Mat(height, width, CvType.CV_8UC1);
        Rect touchedRect = new Rect();
        Scalar blobColorHsv;
        Scalar blobColorRgba;

        Utils.bitmapToMat(bitmap, rgb);

        touchedRect.x = (x > 4) ? x - 4 : 0;
        touchedRect.y = (y > 4) ? y - 4 : 0;

        touchedRect.width = (x + 4 < width) ? x + 4 - touchedRect.x : width - touchedRect.x;
        touchedRect.height = (y + 4 < height) ? y + 4 - touchedRect.y : height - touchedRect.y;

        Mat touchedRegionRgba = rgb.submat(touchedRect);

        Mat touchedRegionHsv = new Mat();
        Imgproc.cvtColor(touchedRegionRgba, touchedRegionHsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Calculate average color of touched region
        blobColorHsv = Core.sumElems(touchedRegionHsv);
        int pointCount = touchedRect.width * touchedRect.height;
        for (int i = 0; i < blobColorHsv.val.length; i++)
            blobColorHsv.val[i] /= pointCount;

        blobColorRgba = convertScalarHsv2Rgba(blobColorHsv);
        return Color.rgb((int) blobColorRgba.val[0], (int) blobColorRgba.val[1], (int) blobColorRgba.val[2]);
    }

    public static Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }

    public static Scalar convertScalarRgba2Hsv(Scalar rgbColor) {
        Mat pointMatRgba = new Mat(1, 1, CvType.CV_8UC3, rgbColor);
        Mat pointMatHsv = new Mat();
        Imgproc.cvtColor(pointMatRgba, pointMatHsv, Imgproc.COLOR_RGB2HSV_FULL, 4);
        return new Scalar(pointMatHsv.get(0, 0));
    }


    public static Mat getRegionOfColor(Bitmap bitmap, int color, int colorRadius) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Mat srcRgb = new Mat(width, height, CvType.CV_8SC1);
        Mat hsvMat = new Mat();
        Mat mask = new Mat();

        Scalar mLowerBound = new Scalar(0);
        Scalar mUpperBound = new Scalar(0);
        Scalar mColorRadius = new Scalar(Color.red(colorRadius), Color.green(colorRadius), Color.blue(colorRadius), 0);

        Scalar rgbColor = new Scalar(Color.red(color), Color.green(color), Color.blue(color), 255);
        Scalar hsvColor = convertScalarRgba2Hsv(rgbColor);

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
        Imgproc.cvtColor(srcRgb, hsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        Core.inRange(hsvMat, mLowerBound, mUpperBound, mask);

        Bitmap result = Bitmap.createBitmap(mask.cols(), mask.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mask, result);

        return mask;
    }

    public static List<MatOfPoint> getContoursOfRegion(Mat mask) {
        Mat mDilatedMask = new Mat();
        Mat mHierarchy = new Mat();
        List<MatOfPoint> result = new ArrayList<MatOfPoint>();
        List<MatOfPoint> allContours = new ArrayList<MatOfPoint>();
        double mMinContourArea = 0.1;

        Imgproc.dilate(mask, mDilatedMask, new Mat());
        Imgproc.findContours(mDilatedMask, allContours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = allContours.iterator();
        while (each.hasNext()) {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        // Filter contours by area and resize to fit the original image size
        each = allContours.iterator();
        while (each.hasNext()) {
            MatOfPoint contour = each.next();
            if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) {
                Core.multiply(contour, new Scalar(2, 2), contour);
                result.add(contour);
            }
        }
        return result;
    }

    public static MatOfPoint getMaxContourOfRegion(Mat mask) {
        Mat mDilatedMask = new Mat();
        Mat mHierarchy = new Mat();
        List<MatOfPoint> result = new ArrayList<MatOfPoint>();
        List<MatOfPoint> allContours = new ArrayList<MatOfPoint>();

        Imgproc.dilate(mask, mDilatedMask, new Mat());
        Imgproc.findContours(mDilatedMask, allContours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find max contour area
        double maxArea = 0;
        int index = -1;
        for (int i = 0; i < allContours.size(); i++) {
            MatOfPoint wrapper = allContours.get(i);
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea) {
                maxArea = area;
                index = i;
            }
        }
        if (index == -1) return null;
        MatOfPoint contour = allContours.get(index);
        Core.multiply(contour, new Scalar(2, 2), contour);
        return contour;
    }

    public static Bitmap drawRegion(Bitmap bitmap, List<MatOfPoint> contours, int color) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Mat srcRgb = new Mat(width, height, CvType.CV_8UC1);
        Scalar rgbColor = new Scalar(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
        Utils.bitmapToMat(bitmap, srcRgb);
        Imgproc.drawContours(srcRgb, contours, -1, rgbColor);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcRgb, result);
        return result;
    }

    public static Bitmap drawRegion(Bitmap bitmap, MatOfPoint contour, int color) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Mat srcRgb = new Mat(width, height, CvType.CV_8UC1);
        Scalar rgbColor = new Scalar(Color.red(color), Color.green(color), Color.blue(color), Color.alpha(color));
        Utils.bitmapToMat(bitmap, srcRgb);
        List<MatOfPoint> contours = new ArrayList<>();
        contours.add(contour);
        Imgproc.drawContours(srcRgb, contours, -1, rgbColor);
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(srcRgb, result);
        return result;
    }
}
