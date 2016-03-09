package net.bingyan.android.wificar.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.bingyan.android.wificar.R;

/**
 *
 * Created by Jinge on 2016/3/2.
 */
public class ImageFragment extends AbstractImageFragment {

    private static final String TAG = "ImageFragment";

    @Override
    protected Bitmap getShowBitmap(Bitmap bitmap) {
        return bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_image, container);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test3);
        imageView = (ImageView) root.findViewById(R.id.imageView);
        return root;
    }
}
