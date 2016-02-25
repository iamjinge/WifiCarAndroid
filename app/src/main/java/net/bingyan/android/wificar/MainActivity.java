package net.bingyan.android.wificar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements GetImageTask.ImageTaskListener, View.OnTouchListener {

    private static final String TAG = "MainActivity";
    private Button showImageButton;
    private ImageView imageView;
    private Button forward;
    private Button left;
    private Button right;
    private Button backward;

    private GetImageTask imageTask;
    private SocketTask socketTask;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        imageTask = new GetImageTask("http://192.168.1.1:8080/?action=stream", this);
        new Thread(imageTask).start();

        socketTask = new SocketTask();

    }

    void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        showImageButton = (Button) findViewById(R.id.showImage);

        showImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageTask.isPause())
                    imageTask.resume();
                else imageTask.pause();
            }
        });

        forward = (Button) findViewById(R.id.moveForward);
        left = (Button) findViewById(R.id.moveLeft);
        right = (Button) findViewById(R.id.moveRight);
        backward = (Button) findViewById(R.id.moveBackward);

        forward.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        backward.setOnTouchListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        imageTask.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageTask.stop();
        socketTask.stop();
    }

    @Override
    public void getImage(final byte[] imageData) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                imageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.moveForward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carForward();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    socketTask.carStop();
                }
                break;
            case R.id.moveLeft:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carLeft();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    socketTask.carStop();
                }
                break;
            case R.id.moveRight:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carRight();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    socketTask.carStop();
                }
                break;
            case R.id.moveBackward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carBackward();
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    socketTask.carStop();
                }
                break;
        }
        return true;
    }
}
