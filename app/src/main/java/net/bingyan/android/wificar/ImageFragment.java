package net.bingyan.android.wificar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jinge on 2016/3/2.
 */
public class ImageFragment extends AbstractImageFragment implements GetImageTask.ImageTaskListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetImageTask.getInstance().addListener(this);
    }

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
