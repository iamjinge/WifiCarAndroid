package net.bingyan.android.wificar.queue;

/**
 * Created by Jinge on 2016/3/10.
 */
public class MotorCode extends SocketCode {

    public int left;
    public int right;

    public MotorCode(int left, int right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String getCode() {
        int rightPwm = (right < 0 ? 256 : 0) + right / 2;
        int leftPwm = (left < 0 ? 256 : 0) + left / 2;
        return CODE_BEGIN + "01" + int2HexString(leftPwm, 2) + int2HexString(rightPwm, 2) + "00 00 00 " + CODE_END;
    }
}
