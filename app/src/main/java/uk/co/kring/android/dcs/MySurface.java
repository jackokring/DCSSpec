package uk.co.kring.android.dcs;

import android.content.Context;
import android.graphics.*;
import androidx.core.graphics.BlendModeColorFilterCompat;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import androidx.core.graphics.BlendModeCompat;
import uk.co.kring.android.dcs.statics.UtilStatic;

public class MySurface extends SurfaceView implements Callback {
    Bitmap[] font;
    Bitmap screen;
    Canvas drawing;
    Paint blend = new Paint();
    Paint bg = new Paint();

    public MySurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        try {
            screen = UtilStatic.getBitmap(context, "font.png");
            screen = screen.copy(Bitmap.Config.ARGB_8888, true);
            drawing = new Canvas(screen);
            font = UtilStatic.getChars();
            bg.setStyle(Paint.Style.FILL);//fill backgrounds
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

    public void charAt(char ch, float x, float y, int fColor, int bColor) {
        bg.setColor(bColor);
        blend.setColorFilter(BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                fColor, BlendModeCompat.SRC_OVER));//on top tint
        x *= UtilStatic.width;
        y *= UtilStatic.height;
        drawing.drawRect(x, y, x + UtilStatic.width,
                y + UtilStatic.height, bg);
        drawing.drawBitmap(font[ch], x, y, blend);
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
