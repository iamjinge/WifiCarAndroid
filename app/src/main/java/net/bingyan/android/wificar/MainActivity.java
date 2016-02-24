package net.bingyan.android.wificar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button button;
    private VideoView videoView;
    private ImageView imageView;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable getStream = new Runnable() {
        @Override
        public void run() {
            int bufSize = 512 * 1024;
            byte[] jpg_buf = new byte[bufSize];
            int readSize = 4096;
            byte[] buffer = new byte[readSize];
            InputStream stream = null;

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://192.168.1.1:8080/?action=stream").openConnection();
                boolean code = true;
                while (code) {
//                    code = false;
                    Log.d(TAG, "get input ");
                    stream = connection.getInputStream();

                    int status = 0;
                    int read;
                    int jpg_count = 0;

                    while ((read = stream.read(buffer, 0, readSize)) > 0) {
                        for (int i = 0; i < read; i++) {
                            switch (status) {
                                //Content-Length:
                                case 0:
                                    if (buffer[i] == (byte) 'C') status++;
                                    else status = 0;
                                    break;
                                case 1:
                                    if (buffer[i] == (byte) 'o') status++;
                                    else status = 0;
                                    break;
                                case 2:
                                    if (buffer[i] == (byte) 'n') status++;
                                    else status = 0;
                                    break;
                                case 3:
                                    if (buffer[i] == (byte) 't') status++;
                                    else status = 0;
                                    break;
                                case 4:
                                    if (buffer[i] == (byte) 'e') status++;
                                    else status = 0;
                                    break;
                                case 5:
                                    if (buffer[i] == (byte) 'n') status++;
                                    else status = 0;
                                    break;
                                case 6:
                                    if (buffer[i] == (byte) 't') status++;
                                    else status = 0;
                                    break;
                                case 7:
                                    if (buffer[i] == (byte) '-') status++;
                                    else status = 0;
                                    break;
                                case 8:
                                    if (buffer[i] == (byte) 'L') status++;
                                    else status = 0;
                                    break;
                                case 9:
                                    if (buffer[i] == (byte) 'e') status++;
                                    else status = 0;
                                    break;
                                case 10:
                                    if (buffer[i] == (byte) 'n') status++;
                                    else status = 0;
                                    break;
                                case 11:
                                    if (buffer[i] == (byte) 'g') status++;
                                    else status = 0;
                                    break;
                                case 12:
                                    if (buffer[i] == (byte) 't') status++;
                                    else status = 0;
                                    break;
                                case 13:
                                    if (buffer[i] == (byte) 'h') status++;
                                    else status = 0;
                                    break;
                                case 14:
                                    if (buffer[i] == (byte) ':') status++;
                                    else status = 0;
                                    break;
                                case 15:
//                                    Log.d(TAG, "status " + status + "  " + buffer[i]);
                                    if (buffer[i] == (byte)0xFF) status++;
                                    jpg_count = 0;
                                    jpg_buf[jpg_count++] = buffer[i];
                                    break;
                                case 16:
//                                    Log.d(TAG, "status " + status + "  " + buffer[i]);
                                    if (buffer[i] == (byte)0xD8) {
                                        status++;
                                        jpg_buf[jpg_count++] = buffer[i];
                                    } else {
                                        if (buffer[i] != (byte)0xFF) status = 15;
                                    }
                                    break;
                                case 17:
//                                    Log.d(TAG, "status " + status + "  " + buffer[i]);
                                    jpg_buf[jpg_count++] = buffer[i];
                                    if (buffer[i] == (byte)0xFF) status++;
                                    if (jpg_count >= bufSize) status = 0;
                                    break;
                                case 18:
//                                    Log.d(TAG, "status " + status + "  " + buffer[i]);
                                    jpg_buf[jpg_count++] = buffer[i];
                                    if (buffer[i] == (byte)0xD9) {
                                        status = 0;
                                        //jpg接收完成
                                        Log.d(TAG, "get image");
                                        final Bitmap bitmap = BitmapFactory.decodeByteArray(jpg_buf, 0, jpg_count);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageView.setImageBitmap(bitmap);
                                            }
                                        });
                                    } else {
                                        if (buffer[i] != (byte)0xFF) status = 17;
                                    }
                                    break;
                                default:
                                    status = 0;
                                    break;

                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        videoView = (VideoView) findViewById(R.id.videoView);
        imageView = (ImageView) findViewById(R.id.imageView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                initVideo();
                new Thread(getStream).start();
//                Picasso.with(MainActivity.this).load("http://192.168.1.1:8080/?action=stream").into(imageView);
            }
        });
    }

    private void initVideo() {
        videoView.setVideoURI(Uri.parse("http://192.168.1.1:8080/?action=stream"));
        videoView.start();
    }
}
