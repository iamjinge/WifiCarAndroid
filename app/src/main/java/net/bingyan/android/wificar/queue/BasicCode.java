package net.bingyan.android.wificar.queue;

/**
 * Created by Jinge on 2016/3/10.
 */
public class BasicCode extends SocketCode {
    public String code;

    public BasicCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
