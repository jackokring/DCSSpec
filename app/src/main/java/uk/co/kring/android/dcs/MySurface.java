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
    Rect screenRect, viewRect;

    public MySurface(Context context) {
        super(context);
        getHolder().addCallback(this);
        try {
            screen = UtilStatic.getBitmap(context, "font.png");
            screen = screen.copy(Bitmap.Config.ARGB_8888, true);
            drawing = new Canvas(screen);
            font = UtilStatic.getChars();
            bg.setStyle(Paint.Style.FILL);//fill backgrounds
            screenRect = new Rect(0, 0, screen.getWidth(), screen.getHeight());
            viewRect = new Rect(0, 0, getWidth(), getHeight());
        } catch(Exception e) {
            throw new ActivityException(e);
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
        bg.setColor(Color.BLACK);
        canvas.drawRect(viewRect, bg);
        canvas.drawBitmap(screen, screenRect,
                viewRect, null);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
        //perform click?
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
        invalidate();
    }

    public static class AttributeMap {
        public int foreground(int i) {
            return Color.WHITE;
        }

        public int background(int i) {
            return Color.BLACK;
        }
    }

    public void stringAt(String s, float x, float y, AttributeMap color) {
        if(color == null) color = new AttributeMap();
        for(int i = 0; i < s.length(); ++i) {
            charAt(s.charAt(i), x, y, color.foreground(i), color.background(i));
            if(x > screen.getWidth() / (float)UtilStatic.width) {
                x = 0F;
                y += UtilStatic.height;//next line LF
            }
        }
    }

    public StringBuilder justify(String s, boolean right, int size, char ch) {
        if(s == null) s = "";
        StringBuilder sb = new StringBuilder();
        if(right) {
            if(s.length() > size) {
                sb.append(s);
            } else {
                while(size - sb.length() > s.length()) sb.append(ch);
                sb.append(s);
            }
        } else {
            sb.append(s);
            while(sb.length() < size) sb.append(ch);
        }
        return sb;
    }

    public int getSizeChars() {
        return screen.getWidth() / UtilStatic.width *
                screen.getHeight() / UtilStatic.height;
    }

    public void stringAtClear(String s, AttributeMap color) {
        s = justify(s, false, getSizeChars(), ' ').toString();
        stringAt(s, 0F, 0F, color);//new page
    }

    public void stringAtClear(StringBuffer s, AttributeMap color) {//internal quick
        stringAt(s.toString(), 0F, 0F, color);//new page
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
