package net.bingyan.android.wificar;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jinge on 2016/2/25.
 */
public class SocketTask {
    private static final String TAG = "SocketTask";
    private Queue<String> cmdQueue = new LinkedBlockingQueue<>();
    private Runnable socketRunnable;

    private boolean stop = false;

    public SocketTask() {
        initRunnable();
        new Thread(socketRunnable).start();
    }

    public void carForward() {
        cmdQueue.add("ff000100ff");
    }

    public void carBackward() {
        cmdQueue.add("ff000200ff");
    }

    public void carRight() {
        cmdQueue.add("ff000300ff");
    }

    public void carLeft() {
        cmdQueue.add("ff000400ff");
    }

    public void carStop() {
        cmdQueue.add("ff000000ff");
    }

    public void stop() {
        stop = true;
    }

    public void initRunnable() {
        socketRunnable = new Runnable() {
            @Override
            public void run() {
                while (!stop) {
                    String cmdStr = cmdQueue.poll();
                    if (!TextUtils.isEmpty(cmdStr)) {
                        try {
                            Socket socket = new Socket("192.168.1.1", 2001);
                            socket.setSoTimeout(5000);
                            Log.d(TAG, "connect");
                            byte[] cmd = hexStringToByteArray(cmdStr);
                            Log.d(TAG, "send : " + Arrays.toString(cmd));
                            socket.getOutputStream().write(cmd);
                            Thread.sleep(50);
                            byte[] input = new byte[32];
                            int i = socket.getInputStream().read(input);
                            Log.d(TAG, "get : " + i + " : " + Arrays.toString(input));
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Log.d(TAG, "over");
            }
        };
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
}
