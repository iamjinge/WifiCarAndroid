package net.bingyan.android.wificar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jinge on 2016/3/9.
 */
public class TouchControlFragment extends AbstractControlFragment {
    private static final String TAG = "TouchControl";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_touch_control, container);
        TouchView touchView = (TouchView) root.findViewById(R.id.touchView);
        touchView.setCallback(new TouchView.TouchCallback() {
            @Override
            public void changeTo(int left, int right) {
                Log.d(TAG, "change to : " + left + "  " + right);
                SocketTask.getInstance().changeMotor(left, right);
            }
        });
        return root;
    }
}
