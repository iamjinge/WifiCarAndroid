package net.bingyan.android.wificar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jinge on 2016/3/3.
 */
public class BasicControlFragment extends AbstractControlFragment implements View.OnTouchListener {
    public static final String BASIC_FORWARD = "ff 00 01 00 ff";
    public static final String BASIC_LEFT = "ff 00 04 00 ff";
    public static final String BASIC_RIGHT = "ff 00 03 00 ff";
    public static final String BASIC_BACKWARD = "ff 00 02 00 ff";
    public static final String BASIC_STOP = "ff 00 00 00 ff";
    private Button forward;
    private Button left;
    private Button right;
    private Button backward;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_normal, container, false);
        forward = (Button) root.findViewById(R.id.moveForward);
        left = (Button) root.findViewById(R.id.moveLeft);
        right = (Button) root.findViewById(R.id.moveRight);
        backward = (Button) root.findViewById(R.id.moveBackward);

        forward.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        backward.setOnTouchListener(this);
        return root;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.moveForward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.addCode(BASIC_FORWARD);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.addCode(BASIC_STOP);
                }
                break;
            case R.id.moveLeft:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.addCode(BASIC_LEFT);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.addCode(BASIC_STOP);
                }
                break;
            case R.id.moveRight:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.addCode(BASIC_RIGHT);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.addCode(BASIC_STOP);
                }
                break;
            case R.id.moveBackward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.addCode(BASIC_BACKWARD);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.addCode(BASIC_STOP);
                }
                break;
        }
        return true;
    }
}
