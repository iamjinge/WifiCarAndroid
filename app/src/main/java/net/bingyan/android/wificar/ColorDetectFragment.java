package net.bingyan.android.wificar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.List;

/**
 * Created by Jinge on 2016/3/3.
 */
public class ColorDetectFragment extends ImageFragment {

    private int color;
    private int radius;

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
            List<MatOfPoint> matOfPoints = BitmapUtil.getContoursOfRegion(mat);
            Bitmap r = BitmapUtil.drawRegion(bitmap, matOfPoints, 0xff000000);
            return r;
        } else {
            return bitmap;
        }
    }
}
