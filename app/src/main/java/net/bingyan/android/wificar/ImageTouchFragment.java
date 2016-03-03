package net.bingyan.android.wificar;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;

import java.util.List;

/**
 * Created by Jinge on 2016/3/2.
 */
public class ImageTouchFragment extends AbstractImageFragment implements View.OnTouchListener, RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "ImageTouchFragment";
    private ColorChosenCallback callback;
    private int redColor;
    private int yellowColor;
    private int blueColor;
    private int currentColor;

    private int redRadius;
    private int yellowRadius;
    private int blueRadius;
    private int currentRadius;

    private TextView hText;
    private TextView sText;
    private TextView vText;
    private SeekBar hSeek;
    private SeekBar sSeek;
    private SeekBar vSeek;

    private RadioGroup colorBtnGroup;
    private RadioButton redBtn;
    private RadioButton yellowBtn;
    private RadioButton blueBtn;

    private SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init();
        View root = inflater.inflate(R.layout.fragment_image_touch, container);
        initView(root);
        return root;
    }

    private void init() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        redColor = preferences.getInt("red_color", 0);
        yellowColor = preferences.getInt("yellow_color", 0);
        blueColor = preferences.getInt("blue_color", 0);

        redRadius = preferences.getInt("red_radius", 0x143232);
        yellowRadius = preferences.getInt("yellow_radius", 0x143232);
        blueRadius = preferences.getInt("blue_radius", 0x143232);

        Log.d(TAG, "radius : red : " + redRadius + "  " + yellowRadius + "  " + blueRadius);
    }

    private void initView(View root) {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test3);
        imageView = (ImageView) root.findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);

        colorBtnGroup = (RadioGroup) root.findViewById(R.id.colorBtnGroup);
        colorBtnGroup.setOnCheckedChangeListener(this);
        redBtn = (RadioButton) root.findViewById(R.id.redBtn);
        yellowBtn = (RadioButton) root.findViewById(R.id.yellowBtn);
        blueBtn = (RadioButton) root.findViewById(R.id.blueBtn);

        redBtn.setBackgroundColor(redColor);
        yellowBtn.setBackgroundColor(yellowColor);
        blueBtn.setBackgroundColor(blueColor);

        hSeek = (SeekBar) root.findViewById(R.id.hSeek);
        sSeek = (SeekBar) root.findViewById(R.id.sSeek);
        vSeek = (SeekBar) root.findViewById(R.id.vSeek);
        hSeek.setOnSeekBarChangeListener(this);
        sSeek.setOnSeekBarChangeListener(this);
        vSeek.setOnSeekBarChangeListener(this);

        hText = (TextView) root.findViewById(R.id.hText);
        sText = (TextView) root.findViewById(R.id.sText);
        vText = (TextView) root.findViewById(R.id.vText);

    }

    public void setCallback(ColorChosenCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "touch ");
            float x = event.getX();
            float y = event.getY();
            int color = BitmapUtil.getColorAtPt(bitmap, x / imageView.getWidth(), y / imageView.getHeight());
            currentColor = color;
            saveColor();
            callback.chooseColor(color);
        }
        return false;
    }

    @Override
    protected Bitmap getShowBitmap(Bitmap bitmap) {
//            imageView.setImageBitmap(BitmapUtil.drawRegion(bitmap, BitmapUtil.getContoursOfRegion(BitmapUtil.getRegionOfColor(bitmap, color)), new Scalar(255,255,255,255)));
        if (currentColor != 0) {
            Mat mat = BitmapUtil.getRegionOfColor(bitmap, currentColor, currentRadius);
            Bitmap b = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, b);
            List<MatOfPoint> matOfPoints = BitmapUtil.getContoursOfRegion(mat);
            Bitmap r = BitmapUtil.drawRegion(bitmap, matOfPoints, 0xff000000);
            return r;
        } else {
            return bitmap;
        }
    }

    private void saveColor() {
        int checkedId = colorBtnGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.redBtn:
                redColor = currentColor;
                redBtn.setBackgroundColor(redColor);
                break;
            case R.id.yellowBtn:
                yellowColor = currentColor;
                yellowBtn.setBackgroundColor(yellowColor);
                break;
            case R.id.blueBtn:
                blueColor = currentColor;
                blueBtn.setBackgroundColor(blueColor);
                break;
        }
    }

    private void saveColorRadius() {
        int checkedId = colorBtnGroup.getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.redBtn:
                redRadius = currentRadius;
                break;
            case R.id.yellowBtn:
                yellowRadius = currentRadius;
                break;
            case R.id.blueBtn:
                blueRadius = currentRadius;
                break;
        }
        Log.d(TAG, "save : " + redRadius + "  " + "  " + yellowRadius + "  " + blueRadius);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.redBtn:
                currentColor = redColor;
                currentRadius = redRadius;
                break;
            case R.id.yellowBtn:
                currentColor = yellowColor;
                currentRadius = yellowRadius;
                break;
            case R.id.blueBtn:
                currentColor = blueColor;
                currentRadius = blueRadius;
                break;
        }

        hSeek.setProgress(Color.red(currentRadius));
        sSeek.setProgress(Color.green(currentRadius));
        vSeek.setProgress(Color.blue(currentRadius));

    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.edit().putInt("red_color", redColor)
                .putInt("yellow_color", yellowColor)
                .putInt("blue_color", blueColor)
                .putInt("red_radius", redRadius)
                .putInt("yellow_radius", yellowRadius)
                .putInt("blue_radius", blueRadius)
                .apply();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.hSeek:
                hText.setText("h: " + progress);
                currentRadius = Color.argb(0, hSeek.getProgress(), Color.green(currentRadius), Color.blue(currentRadius));
                break;
            case R.id.sSeek:
                sText.setText("s: " + progress);
                currentRadius = Color.argb(0, Color.red(currentRadius), sSeek.getProgress(), Color.blue(currentRadius));
                break;
            case R.id.vSeek:
                currentRadius = Color.argb(0, Color.red(currentRadius), Color.green(currentRadius), vSeek.getProgress());
                vText.setText("v: " + progress);
                break;
        }
        saveColorRadius();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface ColorChosenCallback {
        void chooseColor(int color);
    }
}
