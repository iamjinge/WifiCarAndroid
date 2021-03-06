package net.bingyan.android.wificar;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jinge on 2016/2/25.
 */
public class GetImageTask implements Runnable {

    private static final String TAG = "GetImageTask";
    private static GetImageTask instance;
    private String url;
    private List<ImageTaskListener> listenerArrayList = new ArrayList<>();
    private boolean stop = false;
    private boolean pause = false;

    public GetImageTask() {
        stop = true;
    }

    public static GetImageTask getInstance() {
        if (instance == null)
            instance = new GetImageTask();
        return instance;
    }

    public void startTask() {
        if (stop) {
            stop = false;
            pause = false;
            this.url = "http://192.168.1.1:8080/?action=stream";
            new Thread(this).start();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addListener(ImageTaskListener listener) {
        listenerArrayList.add(listener);
    }

    public void removeListener(ImageTaskListener listener) {
        listenerArrayList.remove(listener);
    }

    public void resume() {
        pause = false;
    }

    public void pause() {
        pause = true;
    }

    public void stop() {
        pause = true;
        stop = true;
        listenerArrayList.clear();
        instance = null;
    }

    public boolean isPause() {
        return pause;
    }

    @Override
    public void run() {
        InputStream stream = null;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            while (!stop) {
                Log.d(TAG, "get input ");
                stream = connection.getInputStream();

                int imageDataSize = 524288; //512 * 1024
                byte[] imageData = new byte[imageDataSize];
                byte[] buffer = new byte[2048];
                int status = 0;
                int length;
                int imageDataIndex = 0;

                while (!pause && (length = stream.read(buffer, 0, 2048)) > 0) {
                    for (int i = 0; i < length; i++) {
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
                                if (buffer[i] == (byte) 0xFF) status++;
                                imageDataIndex = 0;
                                imageData[imageDataIndex++] = buffer[i];
                                break;
                            case 16:
                                if (buffer[i] == (byte) 0xD8) {
                                    status++;
                                    imageData[imageDataIndex++] = buffer[i];
                                } else {
                                    if (buffer[i] != (byte) 0xFF) status = 15;
                                }
                                break;
                            case 17:
                                imageData[imageDataIndex++] = buffer[i];
                                if (buffer[i] == (byte) 0xFF) status++;
                                if (imageDataIndex >= imageDataSize) status = 0;
                                break;
                            case 18:
                                imageData[imageDataIndex++] = buffer[i];
                                if (buffer[i] == (byte) 0xD9) {
                                    status = 0;
                                    //jpg接收完成
//                                    Log.d(TAG, "get image " + imageDataIndex);
                                    if (listenerArrayList.size() > 0)
                                        for (ImageTaskListener listener : listenerArrayList)
                                            listener.getImage(imageData, imageDataIndex);
                                } else {
                                    if (buffer[i] != (byte) 0xFF) status = 17;
                                }
                                break;
                            default:
                                status = 0;
                                break;

                        }
                    }
                }
                Thread.sleep(200);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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

    public interface ImageTaskListener {
        void getImage(byte[] imageData, int dataLength);
    }
}
