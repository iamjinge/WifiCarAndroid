package net.bingyan.android.wificar;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Jinge on 2016/2/25.
 */
public class SocketTask {
    private static final String TAG = "SocketTask";
    private static SocketTask instance;
    private Queue<String> cmdQueue = new LinkedBlockingQueue<>();
    private Runnable socketRunnable;
    private boolean stop = false;

    private SocketTask() {
        initRunnable();
    }

    public static SocketTask getInstance() {
        if (instance == null)
            instance = new SocketTask();
        return instance;
    }

    public void start() {
        new Thread(socketRunnable).start();
    }

    public void carForward() {
        cmdQueue.add("62 01 7f 7f 00 00 00 65");
    }

    public void carBackward() {
        cmdQueue.add("62 01 80 80 00 00 00 65");
    }

    public void carRight() {
        cmdQueue.add("62 01 7f 00 00 00 00 65");
    }

    public void carLeft() {
        cmdQueue.add("62 01 00 7f 00 00 00 65");
    }

    public void carStop() {
        cmdQueue.add("62 01 00 00 00 00 00 65");
    }

    public void addCode(String code) {
        cmdQueue.add(code);
    }

    public void stop() {
        stop = true;
        instance = null;
    }

    public void sendAim() {
        cmdQueue.clear();
        cmdQueue.add("62 03 " +
                int2HexString(DataCenter.aimDistance, 4) +
                int2HexString(DataCenter.aimAngle, 2) +
                "00 00 65");
    }

    public void changeMode(int mode) {
        cmdQueue.clear();
        cmdQueue.add("62 10 " + int2HexString(mode, 2) + "00 00 00 00 65");
    }

    public void initRunnable() {
        socketRunnable = new Runnable() {
            @Override
            public void run() {
//                while (!stop) {
                Socket socket = null;
                OutputStream outStream = null;
                InputStream inStream = null;
                try {
                    socket = new Socket("192.168.1.1", 2001);
                    socket.setSoTimeout(5000);
                    outStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                    while (!stop) {
                        byte[] input = new byte[8];
                        int available = inStream.available();
                        if (available > 16) inStream.skip(available - 16);
                        if (inStream.available() > 0) {
                            int num = inStream.read(input);
                            int i = 0;
                            for (i = 0; i < 8; i++) if (input[i] == 0x62) break;
                            if (i > 0) {
                                byte[] addition = new byte[i];
                                num += inStream.read(addition);
                                byte[] tmp = input.clone();
                                for (int index = i; index < 8; index++) {
                                    if (i + index < 8)
                                        input[index] = tmp[i + index];
                                    else input[index] = addition[index + i - 8];
                                }
                            }
                            handleInput(input);
                            Log.d(TAG, "get : " + num + " : " + Arrays.toString(input) + "remains " + inStream.available());
                        }

                        String cmdStr = cmdQueue.poll();
                        if (!TextUtils.isEmpty(cmdStr)) {
                            byte[] cmd = hexStringToByteArray(cmdStr);
                            Log.d(TAG, "send : " + Arrays.toString(cmd));
                            outStream.write(cmd);
                        }

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (socket != null) socket.close();
                        if (outStream != null) outStream.close();
                        if (inStream != null) inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
//                }
                Log.d(TAG, "over");
            }
        };
    }

    private boolean handleInput(byte[] input) {
        boolean inputValid = false;
        if (input[0] == 0x62 && input[7] == 0x65) {
            switch (input[1]) {
                case DataCenter.CODE_GET_AIM:
                    DataCenter.flagGetAim = true;
                    Log.d(TAG, "try to get aim");
                    break;
                case DataCenter.CODE_SEND_AIM:
                    DataCenter.flagGetAim = false;
                    break;
            }
            inputValid = true;
        }
        return inputValid;
    }

    public byte[] hexStringToByteArray(String s) {
        s = s.replace(" ", "");
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String int2HexString(int i, int strLength) {
        String str = Integer.toHexString(i);
        if (str.length() > strLength) str = str.substring(str.length() - strLength);
        else for (int j = str.length(); j < strLength; j++) str = "0" + str;
        return str;
    }
}
