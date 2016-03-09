package net.bingyan.android.wificar;

/**
 * Created by Jinge on 2016/3/3.
 */
public class DataCenter {
    public static final byte CODE_GET_AIM = (byte) 0x03;
    public static final byte CODE_SEND_AIM = (byte) 0x83;

    public static int aimDistance;
    public static int aimAngle;

    public static boolean flagGetAim = false;

}
