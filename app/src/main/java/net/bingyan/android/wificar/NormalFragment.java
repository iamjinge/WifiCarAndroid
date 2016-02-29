package net.bingyan.android.wificar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Jinge on 2016/2/29.
 */
public class NormalFragment extends Fragment implements View.OnTouchListener {

    private Button forward;
    private Button left;
    private Button right;
    private Button backward;

    private SocketTask socketTask;

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

    public void setSocketTask(SocketTask socketTask) {
        this.socketTask = socketTask;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.moveForward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carForward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveLeft:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carLeft();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveRight:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carRight();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
            case R.id.moveBackward:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    socketTask.carBackward();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    socketTask.carStop();
                }
                break;
        }
        return true;
    }
}
