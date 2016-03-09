package net.bingyan.android.wificar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.bingyan.android.wificar.image.ImageTouchFragment;

public class ChooseColorActivity extends AppCompatActivity implements ImageTouchFragment.ColorChosenCallback {
    private static final String TAG = "ChooseColorActivity";
    private ImageTouchFragment imageTouchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_color);

        imageTouchFragment = (ImageTouchFragment) getSupportFragmentManager().findFragmentById(R.id.imageViewFragment);
        imageTouchFragment.setCallback(this);
    }

    @Override
    public void chooseColor(int color) {
        Log.d(TAG,"choose color : " + Color.red(color) + "  " + Color.green(color) + "  " + Color.blue(color));
    }
}
