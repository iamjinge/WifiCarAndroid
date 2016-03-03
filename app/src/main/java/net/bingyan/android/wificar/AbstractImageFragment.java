package net.bingyan.android.wificar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jinge on 2016/3/3.
 */
public abstract class AbstractImageFragment extends Fragment implements GetImageTask.ImageTaskListener {

    protected ImageView imageView;
    protected Bitmap bitmap;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetImageTask.getInstance().addListener(this);
    }

    protected abstract Bitmap getShowBitmap(Bitmap bitmap);

    @Override
    public void getImage(final byte[] imageData, final int dataLength) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                bitmap = BitmapFactory.decodeByteArray(imageData, 0, dataLength);
                if (imageView != null && bitmap != null)
                    imageView.setImageBitmap(getShowBitmap(bitmap));
            }
        });
    }

    protected void saveImage(byte[] imageData, int dataLength) {
        File dir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File f = new File(dir, System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(imageData, 0, dataLength);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
