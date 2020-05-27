package uk.co.kring.android.dcs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class MySurface extends SurfaceView implements Callback {
    Bitmap[] font;
    Bitmap screen;

    public MySurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        try {
            screen = UtilStatic.getBitmap(context, "font.png");
            font = UtilStatic.getChars();
        } catch(Exception e) {
            //error
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas c = getHolder().lockCanvas();
        draw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Canvas c = getHolder().lockCanvas();
        draw(c);
        getHolder().unlockCanvasAndPost(c);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //null
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect copyRect = new Rect(0, 0, screen.getWidth(), screen.getHeight());
        canvas.drawBitmap(screen, copyRect,
                new Rect(0, 0, getWidth(), getHeight()), null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public float x(float x) {//bmp px to width
        return x / screen.getWidth() * getWidth();
    }

    public float y(float y) {//bmp px to height
        return y / screen.getHeight() * getHeight();
    }

    public float ix(float x) {//bmp px from width
        return x * screen.getWidth() / getWidth();
    }

    public float iy(float y) {//bmp px from height
        return y * screen.getHeight() / getHeight();
    }
}
