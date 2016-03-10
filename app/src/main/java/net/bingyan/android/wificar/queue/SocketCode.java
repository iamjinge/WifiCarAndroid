package net.bingyan.android.wificar.queue;

/**
 * Created by Jinge on 2016/3/10.
 */
public abstract class SocketCode {
    public static final String CODE_BEGIN = "62";
    public static final String CODE_END = "65";


    public String int2HexString(int i, int strLength) {
        String str = Integer.toHexString(i);
        if (str.length() > strLength) str = str.substring(str.length() - strLength);
        else for (int j = str.length(); j < strLength; j++) str = "0" + str;
        return str;
    }

    public abstract String getCode();
}
