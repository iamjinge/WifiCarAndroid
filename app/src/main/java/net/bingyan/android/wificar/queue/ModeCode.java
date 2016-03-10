package net.bingyan.android.wificar.queue;

/**
 * Created by Jinge on 2016/3/10.
 */
public class ModeCode extends SocketCode {
    public int mode;

    public ModeCode(int mode) {
        this.mode = mode;
    }

    @Override
    public String getCode() {
        return CODE_BEGIN + "10" + int2HexString(mode, 2)+ "00 00 00 00" + CODE_END;
    }
}
