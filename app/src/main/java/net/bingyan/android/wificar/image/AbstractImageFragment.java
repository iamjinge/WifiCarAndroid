package net.bingyan.android.wificar.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import net.bingyan.android.wificar.GetImageTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Jinge on 2016/3/3.
 */
public abstract class AbstractImageFragment extends Fragment implements GetImageTask.ImageTaskListener {
    public static final int MSG_GET_BITMAP = 0;
    private static final String TAG = "AbstractImageFragment";
    protected ImageView imageView;
    protected Bitmap bitmap;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_BITMAP:
                    Bundle bundle = msg.getData();
                    handleGetImage(bundle.getByteArray("image_data"), bundle.getInt("data_length"));
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        GetImageTask.getInstance().addListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        GetImageTask.getInstance().removeListener(this);
    }

    protected abstract Bitmap getShowBitmap(Bitmap bitmap);

    @Override
    public void getImage(byte[] imageData, int dataLength) {
        Bitmap tmp = BitmapFactory.decodeByteArray(imageData, 0, dataLength);
        bitmap = getShowBitmap(tmp);

        Message msg = handler.obtainMessage(MSG_GET_BITMAP);
        Bundle bundle = new Bundle();
        bundle.putByteArray("image_data", imageData);
        bundle.putInt("data_length", dataLength);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }

    private void handleGetImage(byte[] imageData, int dataLength) {
        if (imageView != null && bitmap != null)
            imageView.setImageBitmap(bitmap);
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
