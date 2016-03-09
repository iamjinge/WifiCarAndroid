package net.bingyan.android.wificar.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.bingyan.android.wificar.R;
import net.bingyan.android.wificar.SocketTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class FreeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_free, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SocketTask.getInstance().changeMode(1);
    }
}
