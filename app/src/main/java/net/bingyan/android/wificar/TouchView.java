package net.bingyan.android.wificar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Jinge on 2016/3/9.
 */
public class TouchView extends View {
    Paint outerCirclePaint;
    Paint innerCirclePaint;

    int colorOut;
    int colorIn;

    int outRadius;
    int inRadius;

    int width;
    int height;

    int right;
    int left;

    int inX;
    int inY;

    TouchCallback callback;

    public TouchView(Context context) {
        super(context);
        init();
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        colorOut = getResources().getColor(R.color.colorPrimary);
        colorIn = getResources().getColor(R.color.colorPrimaryDark);

        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(colorOut);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(18);

        innerCirclePaint = new Paint();
        innerCirclePaint.setColor(colorIn);
        innerCirclePaint.setStyle(Paint.Style.STROKE);
        innerCirclePaint.setStrokeWidth(14);
    }

    void setCallback(TouchCallback callback) {
        this.callback = callback;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = canvas.getWidth();
        height = canvas.getHeight();
        outRadius = width < height ? (int) (width / 2 * 0.8) : (int) (height / 2 * 0.8);
        inRadius = (int) (outRadius * 0.3);

        canvas.drawCircle(width / 2, height / 2, outRadius, outerCirclePaint);
        if (!(inX == 0 && inY == 0))
            canvas.drawCircle(inX, inY, inRadius, innerCirclePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();

            if (Math.hypot(x - width / 2, y - height / 2) < outRadius) {
                inX = (int) x;
                inY = (int) y;
            }
        } else {
            inX = 0;
            inY = 0;
        }

        if (callback != null) {
            if (inX == 0 && inY == 0) {
                callback.changeTo(0, 0);
            } else {
                if (inY < height / 2) {
                    if (inX > width / 2) {
                        double rate = Math.hypot(inX - width / 2, inY - height / 2) / outRadius;
                        callback.changeTo((int) (255 * rate), (int) (Math.atan2(height / 2 - inY, inX - width / 2) / (Math.PI / 2) * 255 * rate));
                    } else {
                        double rate = Math.hypot(inX - width / 2, inY - height / 2) / outRadius;
                        callback.changeTo((int) (Math.atan2(height / 2 - inY, width / 2 - inX) / (Math.PI / 2) * 255 * rate), (int) (255 * rate));
                    }
//                    callback.changeTo((inX - width /2) / outRadius * 255);
                } else {
                    if (inX > width / 2) {
                        double rate = Math.hypot(inX - width / 2, inY - height / 2) / outRadius;
                        callback.changeTo(-(int) (255 * rate), -(int) (Math.atan2(inY - height / 2, inX - width / 2) / (Math.PI / 2) * 255 * rate));
                    } else {
                        double rate = Math.hypot(inX - width / 2, inY - height / 2) / outRadius;
                        callback.changeTo(-(int) (Math.atan2(inY - height / 2, width / 2 - inX) / (Math.PI / 2) * 255 * rate), -(int) (255 * rate));

                    }
                }
            }
        }

        invalidate();
        return true;
    }

    public interface TouchCallback {
        void changeTo(int left, int right);
    }

}
