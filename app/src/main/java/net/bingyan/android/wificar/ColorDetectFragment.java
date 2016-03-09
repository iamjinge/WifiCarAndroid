package net.bingyan.android.wificar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    private int colorYellow;
    private int radiusYellow;
    private int colorBlue;
    private int radiusBlue;
    private int colorRed;
    private int radiusRed;
    private TextView aimText;

    private int distance;
    private int angle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init();
        View root = inflater.inflate(R.layout.fragment_image, container);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test3);
        imageView = (ImageView) root.findViewById(R.id.imageView);
        aimText = (TextView) root.findViewById(R.id.aimText);
        return root;
    }

    void init() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        colorYellow = preferences.getInt("yellow_color", 0);
        radiusYellow = preferences.getInt("yellow_radius", 0x143232);

        colorBlue = preferences.getInt("blue_color", 0);
        radiusBlue = preferences.getInt("blue_radius", 0x143232);

        colorRed = preferences.getInt("red_color", 0);
        radiusRed = preferences.getInt("red_radius", 0x143232);
    }

    @Override
    public void onPause() {
        super.onPause();
        DataCenter.aimDistance = 0;
        if (DataCenter.flagGetAim) {
            SocketTask.getInstance().sendAim();
        }
    }

    @Override
    protected Bitmap getShowBitmap(Bitmap bitmap) {
        if (colorYellow != 0) {
            Mat matYellow = BitmapUtil.getRegionOfColor(bitmap, colorYellow, radiusYellow);
            Bitmap bitmapYellow = Bitmap.createBitmap(matYellow.cols(), matYellow.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(matYellow, bitmapYellow);
            MatOfPoint regionYellow = BitmapUtil.getMaxContourOfRegion(matYellow);
            if (regionYellow == null) return bitmap;
            double areaYellow = Imgproc.contourArea(regionYellow);
            Point[] points = regionYellow.toArray();
            Point sum = new Point(0, 0);
            for (Point p : points) {
                sum.x += p.x;
                sum.y += p.y;
            }
            Point centerYellow = new Point(sum.x / points.length, sum.y / points.length);
            double angleYellow = Math.toDegrees(Math.atan2(960 - 3 * centerYellow.x, 1280));
            double distanceYellow = 3967.2 / Math.sqrt(areaYellow) - 7.2068;
            Log.d(TAG, "detect yellow: " + areaYellow + "  " + (int) angleYellow + "  " + (int) distanceYellow);

            Mat matRed = BitmapUtil.getRegionOfColor(bitmap, colorRed, radiusRed);
            Bitmap bitmapRed = Bitmap.createBitmap(matRed.cols(), matRed.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(matRed, bitmapRed);
            MatOfPoint regionRed = BitmapUtil.getMaxContourOfRegion(matRed);
            if (regionRed == null) return bitmap;
            double areaRed = Imgproc.contourArea(regionRed);
            points = regionRed.toArray();
            sum = new Point(0, 0);
            for (Point p : points) {
                sum.x += p.x;
                sum.y += p.y;
            }
            Point centerRed = new Point(sum.x / points.length, sum.y / points.length);
            double angleRed = Math.toDegrees(Math.atan2(960 - 3 * centerRed.x, 1280));
            double distanceRed = 3967.2 / Math.sqrt(areaRed) - 7.2068;
            Log.d(TAG, "detect Red: " + areaRed + "  " + (int) angleRed + "  " + (int) distanceRed);

            if (distanceRed < 30) {
                DataCenter.aimAngle = (int) angleRed;
                DataCenter.aimDistance = (int) distanceRed;
            } else {
                if (Math.abs(angleRed - angleYellow) < 5 && Math.abs(distanceRed - distanceYellow) / distanceYellow < 0.1) {
                    int angleTmp = (int) ((angleRed + angleYellow) / 2);
                    if (angleTmp < 0) angleTmp += 256;
                    DataCenter.aimAngle = angleTmp;
                    DataCenter.aimDistance = (int) ((distanceYellow + distanceRed) / 2);
                } else {
                    DataCenter.aimDistance = 1;
                }
            }
            if (aimText != null)
                aimText.setText("distance : " + DataCenter.aimDistance + "cm" + "    angle : " + DataCenter.aimAngle);
            if (DataCenter.flagGetAim) {
                SocketTask.getInstance().sendAim();
            }

            Bitmap r = BitmapUtil.drawRegion(bitmap, regionYellow, 0xff000000);
            return r;
        } else {
            return bitmap;
        }
    }
}
