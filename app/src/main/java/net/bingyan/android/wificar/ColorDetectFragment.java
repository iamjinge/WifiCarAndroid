package net.bingyan.android.wificar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Jinge on 2016/3/3.
 */
public class ColorDetectFragment extends ImageFragment {

    private static final String TAG = "ColorDetectFragment";
    private int color;
    private int radius;

    private int distance;
    private int angle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        color = preferences.getInt("yellow_color", 0);
        radius = preferences.getInt("yellow_radius", 0x143232);
    }

    @Override
    protected Bitmap getShowBitmap(Bitmap bitmap) {
        if (color != 0) {
            Mat mat = BitmapUtil.getRegionOfColor(bitmap, color, radius);
            Bitmap b = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, b);
            MatOfPoint region = BitmapUtil.getMaxContourOfRegion(mat);
            if (region == null) return bitmap;
            double area = Imgproc.contourArea(region);
            Point[] points = region.toArray();
            Point sum = new Point(0, 0);
            for (Point p : points) {
                sum.x += p.x;
                sum.y += p.y;
            }
            Point center = new Point(sum.x / points.length, sum.y / points.length);
            double angle = Math.toDegrees(Math.atan2(960 - 3 * center.x, 1280));
            double distance = 3967.2 / Math.sqrt(area) - 7.2068;
            Log.d(TAG, "detect : " + area + "  " + (int) angle + "  " + distance);
            DataCenter.aimAngle = (int) angle;
            DataCenter.aimDistance = (int) distance;
            if (DataCenter.flagGetAim) {
                SocketTask.getInstance().sendAim();
            }

            Bitmap r = BitmapUtil.drawRegion(bitmap, region, 0xff000000);
            return r;
        } else {
            return bitmap;
        }
    }
}
