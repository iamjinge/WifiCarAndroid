package net.bingyan.android.wificar.queue;

/**
 * Created by Jinge on 2016/3/10.
 */
public class AimCode extends SocketCode {
    public int distance;
    public int angle;

    public AimCode(int distance, int angle) {
        this.distance = distance;
        this.angle = angle;
    }

    @Override
    public String getCode() {
        return CODE_BEGIN + "03" + int2HexString(distance, 4) + int2HexString(angle, 2) + "00 00" + CODE_END;
    }
}
