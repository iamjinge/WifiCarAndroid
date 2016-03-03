package net.bingyan.android.wificar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Jinge on 2016/3/3.
 */
public class AbstractControlFragment extends Fragment {

    protected SocketTask socketTask;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketTask = SocketTask.getInstance();
    }
}
